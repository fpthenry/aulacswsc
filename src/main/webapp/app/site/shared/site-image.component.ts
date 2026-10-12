import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';

import { imageSet } from '../core/site-utils';

/**
 * Ảnh tải chậm (lazy) với khung tỷ lệ cố định (không nhảy bố cục) và nền mờ trong lúc tải.
 * Không có ảnh → hiển thị nền hoa văn trang trí.
 *
 *   <site-image [url]="service.imageUrl" [alt]="name" ratio="4 / 3" sizes="(min-width: 768px) 33vw, 100vw" />
 */
@Component({
  selector: 'site-image',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="frame"
      [style.aspect-ratio]="ratio()"
      [style.background-image]="placeholderCss()"
      [class.loaded]="loaded()"
      [class.empty]="!set()"
    >
      @if (set(); as img) {
        <img
          [src]="img.src"
          [attr.srcset]="img.srcset ?? null"
          [attr.sizes]="img.srcset ? sizes() : null"
          [alt]="alt()"
          [attr.loading]="eager() ? 'eager' : 'lazy'"
          [attr.fetchpriority]="eager() ? 'high' : null"
          decoding="async"
          (load)="loaded.set(true)"
          (error)="loaded.set(true)"
        />
      } @else {
        <span class="pattern" aria-hidden="true"></span>
      }
    </div>
  `,
  styles: `
    :host {
      display: block;
    }
    .frame {
      position: relative;
      overflow: hidden;
      border-radius: inherit;
      background: var(--site-surface-2) center / cover no-repeat;
    }
    img {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0;
      transition: opacity 0.5s ease;
    }
    .loaded img {
      opacity: 1;
    }
    /* nền mờ phóng to từ ảnh 24px */
    .frame:not(.loaded):not(.empty)::before {
      content: '';
      position: absolute;
      inset: -10px;
      background: inherit;
      filter: blur(14px);
    }
    .pattern {
      position: absolute;
      inset: 0;
      background:
        radial-gradient(circle at 30% 30%, color-mix(in srgb, var(--site-herb) 35%, transparent) 0 18%, transparent 19%),
        radial-gradient(circle at 70% 65%, color-mix(in srgb, var(--site-gold) 30%, transparent) 0 14%, transparent 15%),
        linear-gradient(135deg, var(--site-surface-2), color-mix(in srgb, var(--site-wood) 18%, var(--site-surface-2)));
    }
    @media (prefers-reduced-motion: reduce) {
      img {
        transition: none;
      }
    }
  `,
})
export class SiteImageComponent {
  readonly url = input<string | null | undefined>(null);
  readonly alt = input('');
  readonly ratio = input('4 / 3');
  readonly sizes = input('100vw');
  /** ảnh đầu trang (LCP): tải ngay, ưu tiên cao */
  readonly eager = input(false);

  readonly loaded = signal(false);
  readonly set = computed(() => imageSet(this.url()));
  readonly placeholderCss = computed(() => {
    const p = this.set()?.placeholder;
    return p ? `url("${p}")` : null;
  });
}
