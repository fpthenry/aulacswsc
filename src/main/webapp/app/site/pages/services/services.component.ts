import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { BookingDraftService } from '../../core/booking-draft.service';
import { SeoService } from '../../core/seo.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDuration, formatPrice, normalizeText } from '../../core/site-utils';
import { RevealDirective } from '../../shared/reveal.directive';
import { ServiceCardComponent } from '../../shared/service-card.component';
import { SiteIconComponent } from '../../shared/site-icon.component';

interface Range {
  key: string;
  min: number;
  max: number;
}

const PRICE_RANGES: Range[] = [
  { key: 'all', min: 0, max: Infinity },
  { key: 'p1', min: 0, max: 400_000 },
  { key: 'p2', min: 400_000, max: 700_000 },
  { key: 'p3', min: 700_000, max: Infinity },
];
const DURATION_RANGES: Range[] = [
  { key: 'all', min: 0, max: Infinity },
  { key: 'd1', min: 0, max: 60 },
  { key: 'd2', min: 61, max: 90 },
  { key: 'd3', min: 91, max: Infinity },
];

@Component({
  selector: 'site-services',
  templateUrl: './services.component.html',
  styleUrl: './services.component.scss',
  imports: [PageHeroComponent, RouterLink, TranslateModule, RevealDirective, ServiceCardComponent, SiteIconComponent],
})
export default class ServicesComponent {
  /** query params (?category=massage&q=...&price=p1&duration=d2) */
  readonly category = input<string>();
  readonly q = input<string>();
  readonly price = input<string>();
  readonly duration = input<string>();

  readonly state = inject(SiteStateService);
  readonly draft = inject(BookingDraftService);
  private readonly router = inject(Router);
  private readonly seo = inject(SeoService);

  readonly priceRanges = PRICE_RANGES;
  readonly durationRanges = DURATION_RANGES;
  readonly search = signal('');

  readonly activeCategory = computed(() => this.category() ?? 'all');
  readonly activePrice = computed(() => PRICE_RANGES.find(r => r.key === this.price()) ?? PRICE_RANGES[0]);
  readonly activeDuration = computed(() => DURATION_RANGES.find(r => r.key === this.duration()) ?? DURATION_RANGES[0]);

  readonly results = computed(() => {
    const cat = this.state.categories().find(c => c.slug === this.activeCategory());
    const term = normalizeText(this.search());
    const price = this.activePrice();
    const duration = this.activeDuration();
    return this.state.services().filter(s => {
      const unit = s.salePrice ?? s.price;
      if (cat && s.category?.id !== cat.id) {
        return false;
      }
      if (unit < price.min || unit >= price.max) {
        return false;
      }
      if (s.durationMinutes < duration.min || s.durationMinutes > duration.max) {
        return false;
      }
      if (term) {
        const haystack = normalizeText(`${s.nameVi} ${s.nameEn} ${s.shortDescriptionVi ?? ''} ${s.shortDescriptionEn ?? ''}`);
        return haystack.includes(term);
      }
      return true;
    });
  });

  constructor() {
    effect(() => this.search.set(this.q() ?? ''));
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'services',
        title: `${this.label('site.services.title')} | ${this.state.siteName()}`,
        description: this.label('site.services.metaDescription'),
      });
    });
  }

  setFilter(key: 'category' | 'price' | 'duration', value: string): void {
    this.router.navigate([], {
      queryParams: { [key]: value === 'all' ? null : value },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }

  submitSearch(): void {
    this.router.navigate([], { queryParams: { q: this.search().trim() || null }, queryParamsHandling: 'merge', replaceUrl: true });
  }

  reset(): void {
    this.search.set('');
    this.router.navigate([], { queryParams: {}, replaceUrl: true });
  }

  rangeLabel(r: Range, kind: 'price' | 'duration'): string {
    const lang = this.state.lang();
    if (r.key === 'all') {
      return this.label('site.services.all');
    }
    const fmt = (v: number): string => (kind === 'price' ? formatPrice(v, lang) : formatDuration(v, lang));
    if (r.max === Infinity) {
      return `≥ ${fmt(r.min)}`;
    }
    if (r.min === 0) {
      return `≤ ${fmt(r.max)}`;
    }
    return `${fmt(r.min)} – ${fmt(r.max)}`;
  }

  private label(key: string): string {
    return this.state.tr(key);
  }
}
