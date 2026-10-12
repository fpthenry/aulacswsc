import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDate } from '../../core/site-utils';
import { Review } from '../../core/site.models';
import { RevealDirective } from '../../shared/reveal.directive';
import { SiteStarsComponent } from '../../shared/review-slider.component';
import { SiteIconComponent } from '../../shared/site-icon.component';

const PAGE_SIZE = 12;

@Component({
  selector: 'site-reviews',
  imports: [PageHeroComponent, RouterLink, TranslateModule, RevealDirective, SiteStarsComponent, SiteIconComponent],
  template: `
    <site-page-hero [image]="state.heroImageFor('reviews')">
      <p class="eyebrow">{{ 'site.home.reviewsEyebrow' | translate }}</p>
      <h1>{{ 'site.reviews.title' | translate }}</h1>
      @if (total()) {
        <p class="lead">{{ 'site.reviews.count' | translate: { count: total() } }}</p>
      }
    </site-page-hero>
    <section class="section">
      <div class="container">
        <div class="masonry">
          @for (r of reviews(); track r.id) {
            <figure class="card review" siteReveal>
              <div class="card-body">
                <site-stars [rating]="r.rating" />
                @if (r.comment) {
                  <blockquote>“{{ r.comment }}”</blockquote>
                }
                @if (r.videoUrl) {
                  <a [href]="r.videoUrl" target="_blank" rel="noopener" class="video-link"
                    ><site-icon name="play" [size]="14" />{{ 'site.reviews.watchVideo' | translate }}</a
                  >
                }
                <figcaption>
                  <strong>{{ r.authorName }}</strong>
                  @if (r.reviewDate) {
                    <span class="muted"> · {{ date(r.reviewDate) }}</span>
                  }
                </figcaption>
              </div>
            </figure>
          } @empty {
            @if (!loading()) {
              <p class="muted">{{ 'site.reviews.empty' | translate }}</p>
            }
          }
        </div>
        @if (hasMore()) {
          <div class="more">
            <button type="button" class="btn-site btn-outline" (click)="loadMore()" [disabled]="loading()">
              {{ 'site.reviews.more' | translate }}
            </button>
          </div>
        }
        <div class="more">
          <a class="btn-site btn-gold" [routerLink]="state.link('booking')">{{ 'site.cta.bookNow' | translate }}</a>
        </div>
      </div>
    </section>
  `,
  styles: `
    .masonry {
      columns: 3 300px;
      column-gap: 1.5rem;
    }
    .review {
      break-inside: avoid;
      margin: 0 0 1.5rem;
    }
    .review:hover {
      transform: none;
    }
    blockquote {
      margin: 0.5rem 0;
      font-family: var(--site-serif);
      font-size: 1.2rem;
      line-height: 1.55;
    }
    .video-link {
      display: inline-flex;
      align-items: center;
      gap: 0.35rem;
      font-size: 0.9rem;
    }
    .more {
      display: flex;
      justify-content: center;
      margin-top: 2rem;
    }
  `,
})
export default class ReviewsComponent implements OnInit {
  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);

  readonly reviews = signal<Review[]>([]);
  readonly total = signal(0);
  readonly loading = signal(false);
  private page = 0;
  readonly hasMore = computed(() => this.reviews().length < this.total());

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      const reviews = this.reviews();
      const avg = reviews.length ? reviews.reduce((s, r) => s + r.rating, 0) / reviews.length : 0;
      this.seo.set({
        lang,
        path: 'reviews',
        title: `${this.state.tr('site.reviews.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.reviews.metaDescription'),
        jsonLd: reviews.length
          ? {
              '@context': 'https://schema.org',
              '@type': 'DaySpa',
              name: this.state.siteName(),
              aggregateRating: {
                '@type': 'AggregateRating',
                ratingValue: avg.toFixed(1),
                reviewCount: this.total(),
              },
              review: reviews.slice(0, 10).map(r => ({
                '@type': 'Review',
                author: { '@type': 'Person', name: r.authorName },
                datePublished: r.reviewDate ?? undefined,
                reviewBody: r.comment ?? undefined,
                reviewRating: { '@type': 'Rating', ratingValue: r.rating, bestRating: 5 },
              })),
            }
          : undefined,
      });
    });
  }

  ngOnInit(): void {
    this.loadMore();
  }

  loadMore(): void {
    this.loading.set(true);
    this.api.reviews(this.page, PAGE_SIZE).subscribe({
      next: res => {
        this.reviews.update(list => [...list, ...res.items]);
        this.total.set(res.total);
        this.page++;
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  date(iso: string): string {
    return formatDate(iso, this.state.lang());
  }
}
