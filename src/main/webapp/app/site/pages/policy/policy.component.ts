import { Component, computed, effect, inject } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteStateService } from '../../core/site-state.service';
import { paragraphs } from '../../core/site-utils';

/** Chính sách đặt lịch / hủy lịch — nội dung sửa trong Site Setting (policyContentVi / policyContentEn). */
@Component({
  selector: 'site-policy',
  imports: [PageHeroComponent, TranslateModule],
  template: `
    <site-page-hero>
      <h1>{{ 'site.nav.policy' | translate }}</h1>
    </site-page-hero>
    <section class="section">
      <div class="container narrow">
        @for (p of content(); track $index) {
          <p>{{ p }}</p>
        } @empty {
          <p class="muted">{{ 'site.policy.empty' | translate }}</p>
        }
      </div>
    </section>
  `,
  styles: '.narrow { max-width: 780px; font-size: 1.05rem; }',
})
export default class PolicyComponent {
  readonly state = inject(SiteStateService);
  private readonly seo = inject(SeoService);
  readonly content = computed(() => paragraphs(this.state.t(this.state.setting(), 'policyContent')));

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({ lang, path: 'policy', title: `${this.state.tr('site.nav.policy')} | ${this.state.siteName()}` });
    });
  }
}
