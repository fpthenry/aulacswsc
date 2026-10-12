import { Component, computed, effect, inject, input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { BookingDraftService } from '../../core/booking-draft.service';
import { SeoService } from '../../core/seo.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDuration, lines, paragraphs, serviceLd } from '../../core/site-utils';
import { RevealDirective } from '../../shared/reveal.directive';
import { ServiceCardComponent, SitePriceComponent } from '../../shared/service-card.component';
import { SiteIconComponent } from '../../shared/site-icon.component';
import { SiteImageComponent } from '../../shared/site-image.component';

@Component({
  selector: 'site-service-detail',
  templateUrl: './service-detail.component.html',
  styleUrl: './service-detail.component.scss',
  imports: [
    PageHeroComponent,
    RouterLink,
    TranslateModule,
    RevealDirective,
    SiteIconComponent,
    SiteImageComponent,
    SitePriceComponent,
    ServiceCardComponent,
  ],
})
export default class ServiceDetailComponent {
  readonly slug = input.required<string>();

  readonly state = inject(SiteStateService);
  readonly draft = inject(BookingDraftService);
  private readonly seo = inject(SeoService);
  private readonly router = inject(Router);

  readonly service = computed(() => this.state.serviceBySlug(this.slug()));
  readonly name = computed(() => this.state.t(this.service(), 'name'));
  readonly description = computed(() => paragraphs(this.state.t(this.service(), 'description')));
  readonly benefits = computed(() => lines(this.state.t(this.service(), 'benefits')));
  readonly notes = computed(() => lines(this.state.t(this.service(), 'notes')));
  readonly duration = computed(() => formatDuration(this.service()?.durationMinutes ?? 0, this.state.lang()));
  readonly selected = computed(() => {
    const s = this.service();
    return !!s && this.draft.draft().serviceIds.includes(s.id);
  });
  readonly category = computed(() => this.state.categories().find(c => c.id === this.service()?.category?.id));
  readonly related = computed(() => {
    const s = this.service();
    return this.state
      .services()
      .filter(o => o.id !== s?.id && o.category?.id === s?.category?.id)
      .slice(0, 3);
  });

  constructor() {
    effect(() => {
      const s = this.service();
      const lang = this.state.lang();
      if (!s) {
        this.seo.set({ lang, path: `services/${this.slug()}`, title: this.state.tr('site.notFound.title'), noindex: true });
        return;
      }
      this.seo.set({
        lang,
        path: `services/${s.slug}`,
        title: `${this.state.t(s, 'name')} | ${this.state.siteName()}`,
        description: this.state.t(s, 'shortDescription'),
        image: s.imageUrl,
        jsonLd: serviceLd(s, this.state.siteName(), location.origin, lang),
      });
    });
  }

  bookNow(): void {
    const s = this.service();
    if (s && !this.selected()) {
      this.draft.toggle(s.id);
    }
    void this.router.navigate(this.state.link('booking'));
  }
}
