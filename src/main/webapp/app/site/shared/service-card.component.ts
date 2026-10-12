import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { BookingDraftService } from '../core/booking-draft.service';
import { SiteStateService } from '../core/site-state.service';
import { formatDuration, formatPrice } from '../core/site-utils';
import { SpaService } from '../core/site.models';
import { SiteIconComponent } from './site-icon.component';
import { SiteImageComponent } from './site-image.component';

/** Giá dịch vụ: giá KM + giá gốc gạch ngang, hoặc khoảng giá, hoặc 1 giá. */
@Component({
  selector: 'site-price',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TranslateModule],
  template: `
    <div class="price">
      @if ((service().salePrice ?? service().price) < service().price) {
        <span class="price-now">{{ fmt(service().salePrice) }}</span>
        <span class="price-old"
          ><span class="visually-hidden">{{ 'site.price.original' | translate }}</span
          >{{ fmt(service().price) }}</span
        >
      } @else if (service().priceMax && service().priceMax! > service().price) {
        <span class="price-now">{{ fmt(service().price) }} – {{ fmt(service().priceMax) }}</span>
      } @else {
        <span class="price-now">{{ fmt(service().price) }}</span>
      }
    </div>
  `,
})
export class SitePriceComponent {
  readonly service = input.required<SpaService>();
  private readonly state = inject(SiteStateService);

  fmt(value: number | null | undefined): string {
    return formatPrice(value, this.state.lang());
  }
}

@Component({
  selector: 'site-service-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, TranslateModule, SiteImageComponent, SiteIconComponent, SitePriceComponent],
  template: `
    <article class="card service-card">
      <a [routerLink]="state.link('services', service().slug)" class="media" tabindex="-1" aria-hidden="true">
        <site-image
          [url]="service().imageUrl"
          [alt]="name()"
          ratio="4 / 3"
          sizes="(min-width: 1024px) 380px, (min-width: 640px) 50vw, 100vw"
        />
        @if (onSale()) {
          <span class="sale-badge">{{ 'site.price.sale' | translate }}</span>
        }
      </a>
      <div class="card-body">
        <div class="meta">
          @if (categoryName()) {
            <span class="tag">{{ categoryName() }}</span>
          }
          <span class="duration"><site-icon name="clock" [size]="15" />{{ duration() }}</span>
        </div>
        <h3>
          <a [routerLink]="state.link('services', service().slug)">{{ name() }}</a>
        </h3>
        <p class="muted desc">{{ state.t(service(), 'shortDescription') }}</p>
        <div class="foot">
          <site-price [service]="service()" />
          <button
            type="button"
            class="btn-site btn-sm"
            [class.btn-primary]="!selected()"
            [class.btn-outline]="selected()"
            [attr.aria-pressed]="selected()"
            (click)="draft.toggle(service().id)"
          >
            <site-icon [name]="selected() ? 'check' : 'plus'" [size]="16" />
            {{ (selected() ? 'site.cta.selected' : 'site.cta.select') | translate }}
            <span class="visually-hidden">{{ name() }}</span>
          </button>
        </div>
      </div>
    </article>
  `,
  styles: `
    .service-card {
      height: 100%;
    }
    .media {
      position: relative;
      display: block;
      overflow: hidden;
    }
    .media site-image {
      transition: transform 0.6s ease;
    }
    .service-card:hover .media site-image {
      transform: scale(1.04);
    }
    .sale-badge {
      position: absolute;
      top: 0.9rem;
      left: 0.9rem;
      padding: 0.2rem 0.75rem;
      border-radius: 999px;
      background: var(--site-gold);
      color: var(--site-on-gold);
      font-size: 0.75rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }
    .meta {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 0.5rem;
    }
    .duration {
      display: inline-flex;
      align-items: center;
      gap: 0.3rem;
      color: var(--site-muted);
      font-size: 0.85rem;
    }
    h3 {
      margin: 0.25rem 0 0;
      font-size: 1.55rem;
      font-weight: 700;
      line-height: 1.2;
    }
    h3 a {
      color: var(--site-wood);
      text-decoration: none;
    }
    h3 a::after {
      content: '';
    }
    .desc {
      flex: 1;
      margin: 0;
      font-size: 0.95rem;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    .foot {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: 0.75rem;
      margin-top: 0.75rem;
    }
    @media (prefers-reduced-motion: reduce) {
      .media site-image {
        transition: none;
      }
    }
  `,
})
export class ServiceCardComponent {
  readonly service = input.required<SpaService>();

  readonly state = inject(SiteStateService);
  readonly draft = inject(BookingDraftService);

  readonly name = computed(() => this.state.t(this.service(), 'name'));
  readonly duration = computed(() => formatDuration(this.service().durationMinutes, this.state.lang()));
  readonly selected = computed(() => this.draft.draft().serviceIds.includes(this.service().id));
  readonly onSale = computed(() => {
    const s = this.service();
    return s.salePrice != null && s.salePrice < s.price;
  });
  readonly categoryName = computed(() => {
    const id = this.service().category?.id;
    const cat = this.state.categories().find(c => c.id === id);
    return cat ? this.state.t(cat, 'name') : '';
  });
}
