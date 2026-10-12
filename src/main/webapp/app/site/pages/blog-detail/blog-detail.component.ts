import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDate, paragraphs } from '../../core/site-utils';
import { BlogPost } from '../../core/site.models';
import { SiteIconComponent } from '../../shared/site-icon.component';
import { SiteImageComponent } from '../../shared/site-image.component';

/** Bài viết: nội dung lưu dạng văn bản thuần, chia đoạn bằng dòng trống (không chèn HTML → an toàn XSS). */
@Component({
  selector: 'site-blog-detail',
  imports: [PageHeroComponent, RouterLink, TranslateModule, SiteIconComponent, SiteImageComponent],
  template: `
    @if (post(); as p) {
      <article>
        <site-page-hero [image]="p.coverImageUrl">
          <p class="meta muted">
            @if (p.category) {
              <span class="tag">{{ 'site.blog.category.' + p.category | translate }}</span>
            }
            <time [attr.datetime]="p.publishedAt">{{ date(p.publishedAt) }}</time>
          </p>
          <h1>{{ state.t(p, 'title') }}</h1>
          @if (state.t(p, 'excerpt')) {
            <p class="lead">{{ state.t(p, 'excerpt') }}</p>
          }
        </site-page-hero>
        <div class="section body">
          <div class="container narrow">
            @if (p.coverImageUrl) {
              <site-image
                class="cover"
                [url]="p.coverImageUrl"
                [alt]="state.t(p, 'title')"
                ratio="16 / 9"
                sizes="(min-width: 800px) 760px, 100vw"
                [eager]="true"
              />
            }
            @for (para of content(); track $index) {
              <p>{{ para }}</p>
            }
            <div class="actions">
              <a class="btn-site btn-outline" [routerLink]="state.link('blog')"
                ><site-icon name="left" [size]="16" />{{ 'site.blog.back' | translate }}</a
              >
              <a class="btn-site btn-gold" [routerLink]="state.link('booking')">{{ 'site.cta.bookNow' | translate }}</a>
            </div>
          </div>
        </div>
      </article>
    } @else if (notFound()) {
      <site-page-hero>
        <h1>{{ 'site.notFound.title' | translate }}</h1>
        <a class="btn-site btn-primary" [routerLink]="state.link('blog')">{{ 'site.blog.back' | translate }}</a>
      </site-page-hero>
    } @else {
      <site-page-hero><span class="spinner" role="status" [attr.aria-label]="'site.a11y.loading' | translate"></span></site-page-hero>
    }
  `,
  styles: `
    .narrow {
      max-width: 780px;
    }
    .meta {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 0.6rem;
      font-size: 0.9rem;
    }
    .cover {
      margin-bottom: 2rem;
      border-radius: var(--site-radius);
      overflow: hidden;
    }
    .body p {
      font-size: 1.08rem;
      line-height: 1.85;
    }
    .actions {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      gap: 0.75rem;
      margin-top: 2.5rem;
    }
    .spinner {
      display: inline-block;
    }
  `,
})
export default class BlogDetailComponent {
  readonly slug = input.required<string>();

  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);

  readonly post = signal<BlogPost | null>(null);
  readonly notFound = signal(false);
  readonly content = computed(() => paragraphs(this.state.t(this.post(), 'content')));

  constructor() {
    effect(() => {
      const slug = this.slug();
      this.post.set(null);
      this.notFound.set(false);
      this.api.blogPost(slug).subscribe({
        next: p => this.post.set(p),
        error: () => this.notFound.set(true),
      });
    });
    effect(() => {
      const p = this.post();
      const lang = this.state.lang();
      if (!p) {
        return;
      }
      const title = this.state.t(p, 'title');
      this.seo.set({
        lang,
        path: `blog/${p.slug}`,
        title: `${title} | ${this.state.siteName()}`,
        description: this.state.t(p, 'excerpt'),
        image: p.coverImageUrl,
        type: 'article',
        jsonLd: {
          '@context': 'https://schema.org',
          '@type': 'BlogPosting',
          headline: title,
          datePublished: p.publishedAt ?? undefined,
          image: p.coverImageUrl ? new URL(p.coverImageUrl, location.origin).toString() : undefined,
          publisher: { '@type': 'Organization', name: this.state.siteName() },
        },
      });
    });
  }

  date(iso: string | null | undefined): string {
    return formatDate(iso, this.state.lang());
  }
}
