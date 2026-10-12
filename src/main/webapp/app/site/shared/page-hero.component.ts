import { Component, OnDestroy, OnInit, computed, inject, input } from '@angular/core';

import { SiteStateService } from '../core/site-state.service';
import { imageSet } from '../core/site-utils';
import { SiteIconComponent } from './site-icon.component';

/**
 * Khối đầu trang của các trang con (Dịch vụ, Cơ sở, Tin tức...): nền tối + ảnh zoom chậm + chữ sáng,
 * cùng phong cách hero trang chủ. Nội dung (eyebrow, h1, đoạn dẫn, nút...) truyền qua <ng-content>.
 * Style nằm trong layout/site.scss (.page-hero--dark).
 *
 *   <site-page-hero [image]="state.heroImageFor('services')">
 *     <p class="eyebrow">…</p><h1>…</h1><p class="lead">…</p>
 *   </site-page-hero>
 */
@Component({
  selector: 'site-page-hero',
  imports: [SiteIconComponent],
  template: `
    <section class="page-hero page-hero--dark" [class.has-image]="!!img()">
      @if (img(); as i) {
        <div class="ph-media" aria-hidden="true">
          <img [src]="i.src" [attr.srcset]="i.srcset ?? null" sizes="100vw" alt="" fetchpriority="high" decoding="async" />
        </div>
      }
      <span class="ph-veil" aria-hidden="true"></span>
      <div class="container ph-content">
        <div class="ph-ornament" aria-hidden="true"><span></span><site-icon name="leaf" [size]="16" /><span></span></div>
        <ng-content />
      </div>
    </section>
  `,
  styles: ':host { display: block; }',
})
export class PageHeroComponent implements OnInit, OnDestroy {
  /** ảnh nền (null = nền tối gradient) */
  readonly image = input<string | null | undefined>(null);
  readonly img = computed(() => imageSet(this.image()));

  private readonly state = inject(SiteStateService);
  private unregister?: () => void;

  ngOnInit(): void {
    // báo cho header: đang có nền tối ở đầu trang → chữ sáng, nền trong suốt
    this.unregister = this.state.registerDarkHero();
  }

  ngOnDestroy(): void {
    this.unregister?.();
  }
}
