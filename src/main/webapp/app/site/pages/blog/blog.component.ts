import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDate } from '../../core/site-utils';
import { BlogCategory, BlogPost } from '../../core/site.models';
import { RevealDirective } from '../../shared/reveal.directive';
import { SiteImageComponent } from '../../shared/site-image.component';

const PAGE_SIZE = 9;
const CATEGORIES: BlogCategory[] = ['NEWS', 'EVENT', 'REVIEW', 'TIPS'];

@Component({
  selector: 'site-blog',
  imports: [PageHeroComponent, RouterLink, TranslateModule, RevealDirective, SiteImageComponent],
  template: `
    <site-page-hero [image]="heroImage()">
      <p class="eyebrow">{{ 'site.home.blogEyebrow' | translate }}</p>
      <h1>{{ 'site.blog.title' | translate }}</h1>
    </site-page-hero>
    <section class="section">
      <div class="container">
        <div class="chips filter" role="group" [attr.aria-label]="'site.blog.filter' | translate">
          <button type="button" class="chip" [attr.aria-pressed]="!activeCategory()" (click)="setCategory(null)">
            {{ 'site.services.all' | translate }}
          </button>
          @for (c of categories; track c) {
            <button type="button" class="chip" [attr.aria-pressed]="activeCategory() === c" (click)="setCategory(c)">
              {{ 'site.blog.category.' + c | translate }}
            </button>
          }
        </div>

        <div class="grid grid-3" aria-live="polite" [attr.aria-busy]="loading()">
          @for (p of posts(); track p.id; let i = $index) {
            <article class="card" siteReveal [revealDelay]="(i % 3) * 80">
              <site-image [url]="p.coverImageUrl" [alt]="state.t(p, 'title')" ratio="16 / 10" sizes="(min-width: 1024px) 380px, 100vw" />
              <div class="card-body">
                <p class="meta muted">
                  @if (p.category) {
                    <span class="tag">{{ 'site.blog.category.' + p.category | translate }}</span>
                  }
                  <time [attr.datetime]="p.publishedAt">{{ date(p.publishedAt) }}</time>
                </p>
                <h2 class="title">
                  <a [routerLink]="state.link('blog', p.slug)">{{ state.t(p, 'title') }}</a>
                </h2>
                <p class="muted">{{ state.t(p, 'excerpt') }}</p>
              </div>
            </article>
          } @empty {
            @if (!loading()) {
              <p class="muted">{{ 'site.blog.empty' | translate }}</p>
            }
          }
        </div>

        @if (pages() > 1) {
          <nav class="pager" [attr.aria-label]="'site.a11y.pagination' | translate">
            <button type="button" class="btn-site btn-outline btn-sm" [disabled]="pageIndex() === 0" (click)="go(pageIndex() - 1)">
              {{ 'site.a11y.previous' | translate }}
            </button>
            @for (n of pageNumbers(); track n) {
              <button
                type="button"
                class="page-btn"
                [attr.aria-current]="n === pageIndex() ? 'page' : null"
                (click)="go(n)"
                [attr.aria-label]="'site.blog.page' | translate: { page: n + 1 }"
              >
                {{ n + 1 }}
              </button>
            }
            <button type="button" class="btn-site btn-outline btn-sm" [disabled]="pageIndex() >= pages() - 1" (click)="go(pageIndex() + 1)">
              {{ 'site.a11y.next' | translate }}
            </button>
          </nav>
        }
      </div>
    </section>
  `,
  styles: `
    .filter {
      justify-content: center;
      margin-bottom: 2rem;
    }
    .meta {
      display: flex;
      align-items: center;
      gap: 0.6rem;
      margin: 0;
      font-size: 0.85rem;
    }
    .title {
      font-size: 1.45rem;
      margin: 0.25rem 0;
    }
    .title a {
      color: var(--site-wood);
      text-decoration: none;
    }
    .title a:hover {
      text-decoration: underline;
    }
    .pager {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      align-items: center;
      gap: 0.5rem;
      margin-top: 2.5rem;
    }
    .page-btn {
      width: 44px;
      height: 44px;
      border: 1px solid var(--site-border);
      border-radius: 50%;
      background: var(--site-surface);
      color: var(--site-text);
      cursor: pointer;
    }
    .page-btn[aria-current='page'] {
      background: var(--site-wood);
      border-color: var(--site-wood);
      color: var(--site-on-wood);
    }
  `,
})
export default class BlogComponent {
  /** ?page=2&category=EVENT */
  readonly page = input<string>();
  readonly category = input<string>();

  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);
  private readonly router = inject(Router);

  readonly categories = CATEGORIES;
  readonly posts = signal<BlogPost[]>([]);
  readonly total = signal(0);
  readonly loading = signal(false);
  readonly heroImage = computed(() => this.posts().find(p => p.coverImageUrl)?.coverImageUrl ?? this.state.heroImageFor('blog'));

  readonly pageIndex = computed(() => Math.max(0, (Number(this.page()) || 1) - 1));
  readonly activeCategory = computed(() =>
    CATEGORIES.includes(this.category() as BlogCategory) ? (this.category() as BlogCategory) : null,
  );
  readonly pages = computed(() => Math.ceil(this.total() / PAGE_SIZE));
  readonly pageNumbers = computed(() => Array.from({ length: this.pages() }, (_, i) => i));

  constructor() {
    effect(() => {
      const page = this.pageIndex();
      const category = this.activeCategory();
      this.loading.set(true);
      this.api.blog(page, PAGE_SIZE, category).subscribe({
        next: res => {
          this.posts.set(res.items);
          this.total.set(res.total);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
    });
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'blog',
        title: `${this.state.tr('site.blog.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.blog.metaDescription'),
      });
    });
  }

  go(n: number): void {
    void this.router.navigate([], { queryParams: { page: n > 0 ? n + 1 : null }, queryParamsHandling: 'merge' });
  }

  setCategory(c: BlogCategory | null): void {
    void this.router.navigate([], { queryParams: { category: c, page: null }, queryParamsHandling: 'merge' });
  }

  date(iso: string | null | undefined): string {
    return formatDate(iso, this.state.lang());
  }
}
