import { Component, ElementRef, HostListener, OnInit, computed, effect, inject, signal, viewChild } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { imageSet } from '../../core/site-utils';
import { GalleryImage } from '../../core/site.models';
import { RevealDirective } from '../../shared/reveal.directive';
import { SiteIconComponent } from '../../shared/site-icon.component';
import { SiteImageComponent } from '../../shared/site-image.component';

/**
 * Thư viện ảnh: lưới masonry (CSS columns) + lightbox dùng thẻ <dialog>
 * (bàn phím ←/→/Esc, vuốt trái/phải trên điện thoại).
 */
@Component({
  selector: 'site-gallery',
  imports: [PageHeroComponent, TranslateModule, RevealDirective, SiteIconComponent, SiteImageComponent],
  template: `
    <site-page-hero [image]="heroImage()">
      <p class="eyebrow">{{ 'site.home.galleryEyebrow' | translate }}</p>
      <h1>{{ 'site.gallery.title' | translate }}</h1>
    </site-page-hero>
    <section class="section">
      <div class="container">
        @if (images().length) {
          <ul class="masonry">
            @for (img of images(); track img.id; let i = $index) {
              <li siteReveal [revealDelay]="(i % 4) * 60">
                <button
                  type="button"
                  class="thumb"
                  (click)="open(i)"
                  [attr.aria-label]="('site.gallery.open' | translate) + ': ' + state.t(img, 'alt')"
                >
                  <site-image
                    [url]="img.imageUrl"
                    [alt]="state.t(img, 'alt')"
                    [ratio]="img.width && img.height ? img.width + ' / ' + img.height : '4 / 3'"
                    sizes="(min-width: 1024px) 300px, (min-width: 640px) 45vw, 100vw"
                  />
                </button>
                @if (state.t(img, 'caption')) {
                  <p class="caption muted">{{ state.t(img, 'caption') }}</p>
                }
              </li>
            }
          </ul>
        } @else if (loaded()) {
          <p class="muted center">{{ 'site.gallery.empty' | translate }}</p>
        }
      </div>
    </section>

    <dialog
      #dialog
      class="lightbox"
      (close)="index.set(null)"
      (click)="onBackdrop($event)"
      [attr.aria-label]="'site.gallery.title' | translate"
    >
      @if (current(); as img) {
        <figure (touchstart)="touchStart($event)" (touchend)="touchEnd($event)">
          <img [src]="full(img)" [alt]="state.t(img, 'alt')" />
          <figcaption>
            <span>{{ state.t(img, 'caption') || state.t(img, 'alt') }}</span>
            <span class="count">{{ (index() ?? 0) + 1 }} / {{ images().length }}</span>
          </figcaption>
        </figure>
        <button type="button" class="lb-btn lb-close" (click)="close()" [attr.aria-label]="'site.a11y.close' | translate">
          <site-icon name="close" />
        </button>
        <button type="button" class="lb-btn lb-prev" (click)="step(-1)" [attr.aria-label]="'site.a11y.previous' | translate">
          <site-icon name="left" [size]="26" />
        </button>
        <button type="button" class="lb-btn lb-next" (click)="step(1)" [attr.aria-label]="'site.a11y.next' | translate">
          <site-icon name="right" [size]="26" />
        </button>
      }
    </dialog>
  `,
  styles: `
    .masonry {
      columns: 4 240px;
      column-gap: 1rem;
      margin: 0;
      padding: 0;
      list-style: none;
    }
    .masonry li {
      break-inside: avoid;
      margin-bottom: 1rem;
    }
    .thumb {
      display: block;
      width: 100%;
      padding: 0;
      border: 0;
      border-radius: var(--site-radius-sm);
      overflow: hidden;
      background: none;
      cursor: zoom-in;
    }
    .thumb site-image {
      transition: transform 0.5s ease;
    }
    .thumb:hover site-image {
      transform: scale(1.03);
    }
    .caption {
      margin: 0.4rem 0 0;
      font-size: 0.85rem;
    }
    .center {
      text-align: center;
    }
    .lightbox {
      width: 100vw;
      max-width: 100vw;
      height: 100vh;
      max-height: 100vh;
      margin: 0;
      padding: 0;
      border: 0;
      background: rgb(20 15 11 / 94%);
      color: #f7f1e8;
    }
    .lightbox::backdrop {
      background: rgb(0 0 0 / 60%);
    }
    figure {
      display: grid;
      place-items: center;
      gap: 0.75rem;
      height: 100%;
      margin: 0;
      padding: 4rem 1rem 1.5rem;
    }
    figure img {
      max-width: min(100%, 1400px);
      max-height: calc(100vh - 9rem);
      object-fit: contain;
      border-radius: 6px;
    }
    figcaption {
      display: flex;
      gap: 1.5rem;
      font-size: 0.9rem;
    }
    .count {
      opacity: 0.7;
    }
    .lb-btn {
      position: absolute;
      display: grid;
      place-items: center;
      width: 52px;
      height: 52px;
      border: 1px solid rgb(247 241 232 / 30%);
      border-radius: 50%;
      background: rgb(20 15 11 / 60%);
      color: inherit;
      cursor: pointer;
    }
    .lb-close {
      top: 1rem;
      right: 1rem;
    }
    .lb-prev,
    .lb-next {
      top: 50%;
      transform: translateY(-50%);
    }
    .lb-prev {
      left: 1rem;
    }
    .lb-next {
      right: 1rem;
    }
    @media (prefers-reduced-motion: reduce) {
      .thumb site-image {
        transition: none;
      }
    }
  `,
})
export default class GalleryComponent implements OnInit {
  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);

  readonly images = signal<GalleryImage[]>([]);
  readonly loaded = signal(false);
  readonly heroImage = computed(
    () => this.images().find(i => i.width && i.height && i.width > i.height)?.imageUrl ?? this.state.heroImageFor('gallery'),
  );
  readonly index = signal<number | null>(null);
  readonly current = computed(() => {
    const i = this.index();
    return i === null ? null : this.images()[i];
  });

  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');
  private touchX = 0;
  private opener: HTMLElement | null = null;

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'gallery',
        title: `${this.state.tr('site.gallery.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.gallery.metaDescription'),
        image: this.images()[0]?.imageUrl,
      });
    });
  }

  ngOnInit(): void {
    this.api.gallery().subscribe({
      next: imgs => {
        this.images.set(imgs);
        this.loaded.set(true);
      },
      error: () => this.loaded.set(true),
    });
  }

  open(i: number): void {
    this.opener = document.activeElement as HTMLElement | null;
    this.index.set(i);
    this.dialog().nativeElement.showModal();
  }

  close(): void {
    this.dialog().nativeElement.close();
    // trả focus về ảnh đã mở (thân thiện bàn phím)
    this.opener?.focus();
  }

  step(delta: number): void {
    const n = this.images().length;
    const i = this.index();
    if (i !== null && n) {
      this.index.set((i + delta + n) % n);
    }
  }

  @HostListener('document:keydown', ['$event'])
  onKey(e: KeyboardEvent): void {
    if (this.index() === null) {
      return;
    }
    if (e.key === 'ArrowLeft') {
      this.step(-1);
    } else if (e.key === 'ArrowRight') {
      this.step(1);
    }
  }

  onBackdrop(e: MouseEvent): void {
    if (e.target === this.dialog().nativeElement) {
      this.close();
    }
  }

  touchStart(e: TouchEvent): void {
    this.touchX = e.changedTouches[0].clientX;
  }

  touchEnd(e: TouchEvent): void {
    const dx = e.changedTouches[0].clientX - this.touchX;
    if (Math.abs(dx) > 50) {
      this.step(dx < 0 ? 1 : -1);
    }
  }

  full(img: GalleryImage): string {
    return imageSet(img.imageUrl)?.src ?? img.imageUrl;
  }
}
