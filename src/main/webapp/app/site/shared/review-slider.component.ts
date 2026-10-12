import { Component, ElementRef, OnInit, inject, input, signal, viewChild } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TranslateModule } from '@ngx-translate/core';

import { SiteApiService } from '../core/site-api.service';
import { SiteStateService } from '../core/site-state.service';
import { formatDate, youtubeId } from '../core/site-utils';
import { Review } from '../core/site.models';
import { SiteIconComponent } from './site-icon.component';

/** Sao đánh giá (đọc được bằng trình đọc màn hình). */
@Component({
  selector: 'site-stars',
  imports: [TranslateModule, SiteIconComponent],
  template: `<span class="stars" role="img" [attr.aria-label]="'site.reviews.rating' | translate: { rating: rating() }">
    @for (i of [1, 2, 3, 4, 5]; track i) {
      <site-icon name="star" [size]="16" [class.on]="i <= rating()" />
    }
  </span>`,
  styles: `
    .stars {
      display: inline-flex;
      gap: 2px;
      color: var(--site-border);
    }
    .on {
      color: var(--site-gold);
    }
    .on ::ng-deep path {
      fill: currentColor;
    }
  `,
})
export class SiteStarsComponent {
  readonly rating = input(5);
}

/**
 * Slider đánh giá: cuộn ngang bằng CSS scroll-snap (vuốt trên mobile), nút trước/sau cho bàn phím/chuột.
 */
@Component({
  selector: 'site-review-slider',
  imports: [TranslateModule, SiteIconComponent, SiteStarsComponent],
  template: `
    @if (reviews().length) {
      <div class="slider" role="region" [attr.aria-label]="'site.home.reviewsTitle' | translate">
        <ul class="track" #track tabindex="0">
          @for (r of reviews(); track r.id) {
            <li class="slide">
              <figure class="review card">
                @if (ytId(r); as id) {
                  <div class="video">
                    @if (playing() === r.id) {
                      <iframe
                        [src]="embed(id)"
                        [title]="r.authorName"
                        allow="accelerometer; autoplay; encrypted-media; picture-in-picture"
                        allowfullscreen
                        loading="lazy"
                      ></iframe>
                    } @else {
                      <button
                        type="button"
                        class="play"
                        (click)="playing.set(r.id)"
                        [attr.aria-label]="'site.reviews.playVideo' | translate: { name: r.authorName }"
                      >
                        <img [src]="'https://i.ytimg.com/vi/' + id + '/hqdefault.jpg'" alt="" loading="lazy" />
                        <span class="play-icon"><site-icon name="play" [size]="22" /></span>
                      </button>
                    }
                  </div>
                }
                <div class="card-body">
                  <site-stars [rating]="r.rating" />
                  @if (r.comment) {
                    <blockquote>“{{ r.comment }}”</blockquote>
                  }
                  @if (r.videoUrl && !ytId(r)) {
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
            </li>
          }
        </ul>
        <div class="controls">
          <button type="button" class="icon-btn" (click)="scroll(-1)" [attr.aria-label]="'site.a11y.previous' | translate">
            <site-icon name="left" />
          </button>
          <button type="button" class="icon-btn" (click)="scroll(1)" [attr.aria-label]="'site.a11y.next' | translate">
            <site-icon name="right" />
          </button>
        </div>
      </div>
    } @else if (loaded()) {
      <p class="muted center">{{ 'site.reviews.empty' | translate }}</p>
    }
  `,
  styles: `
    .track {
      display: grid;
      grid-auto-flow: column;
      grid-auto-columns: min(88%, 380px);
      gap: 1.25rem;
      margin: 0;
      padding: 0.5rem 0.25rem 1.5rem;
      list-style: none;
      overflow-x: auto;
      scroll-snap-type: x mandatory;
      scroll-behavior: smooth;
      overscroll-behavior-x: contain;
      scrollbar-width: thin;
    }
    .slide {
      scroll-snap-align: start;
    }
    .review {
      height: 100%;
      margin: 0;
    }
    blockquote {
      flex: 1;
      margin: 0.5rem 0;
      font-family: var(--site-serif);
      font-size: 1.25rem;
      line-height: 1.5;
      color: var(--site-text);
    }
    figcaption {
      font-size: 0.9rem;
    }
    .video {
      position: relative;
      aspect-ratio: 16 / 9;
      background: #000;
    }
    .video iframe,
    .play,
    .play img {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
      border: 0;
      object-fit: cover;
    }
    .play {
      padding: 0;
      cursor: pointer;
      background: none;
    }
    .play-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      display: grid;
      place-items: center;
      width: 58px;
      height: 58px;
      border-radius: 50%;
      background: rgb(0 0 0 / 60%);
      color: #fff;
      transform: translate(-50%, -50%);
    }
    .video-link {
      display: inline-flex;
      align-items: center;
      gap: 0.35rem;
      font-size: 0.9rem;
    }
    .controls {
      display: flex;
      justify-content: center;
      gap: 0.75rem;
    }
    .center {
      text-align: center;
    }
    @media (prefers-reduced-motion: reduce) {
      .track {
        scroll-behavior: auto;
      }
    }
  `,
})
export class ReviewSliderComponent implements OnInit {
  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly sanitizer = inject(DomSanitizer);

  readonly reviews = signal<Review[]>([]);
  readonly loaded = signal(false);
  readonly playing = signal<number | null>(null);
  private readonly track = viewChild<ElementRef<HTMLElement>>('track');

  ngOnInit(): void {
    this.api.reviews(0, 10).subscribe({
      next: page => {
        this.reviews.set(page.items);
        this.loaded.set(true);
      },
      error: () => this.loaded.set(true),
    });
  }

  scroll(direction: number): void {
    const el = this.track()?.nativeElement;
    if (el) {
      el.scrollBy({ left: direction * el.clientWidth * 0.9, behavior: 'smooth' });
    }
  }

  ytId(r: Review): string | null {
    return youtubeId(r.videoUrl);
  }

  /** Chỉ nhúng youtube-nocookie với id đã kiểm tra bằng regex → an toàn để bypass sanitizer. */
  private readonly embeds = new Map<string, SafeResourceUrl>();

  embed(id: string): SafeResourceUrl {
    let url = this.embeds.get(id);
    if (!url) {
      url = this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube-nocookie.com/embed/${id}?autoplay=1&rel=0`);
      this.embeds.set(id, url);
    }
    return url;
  }

  date(iso: string): string {
    return formatDate(iso, this.state.lang());
  }
}
