import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { formatDate, formatPrice, formatTime, normalizeVnPhone } from '../../core/site-utils';
import { BookingConfirmation } from '../../core/site.models';

/** Tra cứu lịch hẹn bằng mã đặt lịch + số điện thoại. */
@Component({
  selector: 'site-booking-lookup',
  imports: [PageHeroComponent, RouterLink, TranslateModule],
  template: `
    <site-page-hero>
      <p class="eyebrow">{{ 'site.booking.eyebrow' | translate }}</p>
      <h1>{{ 'site.lookup.title' | translate }}</h1>
      <p class="lead">{{ 'site.lookup.intro' | translate }}</p>
    </site-page-hero>
    <section class="section">
      <div class="container narrow">
        <form class="card form" (submit)="$event.preventDefault(); search()" novalidate>
          <div class="card-body">
            <div class="field">
              <label for="l-code">{{ 'site.booking.code' | translate }}</label>
              <input
                id="l-code"
                class="input code-input"
                autocomplete="off"
                placeholder="AL261011ABCD"
                [value]="code()"
                (input)="code.set($any($event.target).value)"
                [attr.aria-invalid]="showErrors() && !code().trim()"
                aria-describedby="l-code-err"
              />
              @if (showErrors() && !code().trim()) {
                <span id="l-code-err" class="error-text" role="alert">{{ 'site.lookup.codeRequired' | translate }}</span>
              }
            </div>
            <div class="field">
              <label for="l-phone">{{ 'site.booking.phone' | translate }}</label>
              <input
                id="l-phone"
                class="input"
                type="tel"
                autocomplete="tel"
                [value]="phone()"
                (input)="phone.set($any($event.target).value)"
                [attr.aria-invalid]="showErrors() && !phoneOk()"
                aria-describedby="l-phone-err"
              />
              @if (showErrors() && !phoneOk()) {
                <span id="l-phone-err" class="error-text" role="alert">{{ 'site.booking.errors.phone' | translate }}</span>
              }
            </div>
            <button type="submit" class="btn-site btn-primary" [disabled]="loading()">{{ 'site.lookup.search' | translate }}</button>
          </div>
        </form>

        <div aria-live="polite">
          @if (error(); as e) {
            <div class="alert-box" role="alert">{{ 'site.lookup.errors.' + e | translate }}</div>
          }
          @if (result(); as r) {
            <div class="card result">
              <div class="card-body">
                <p class="status">
                  <span class="tag">{{ 'site.lookup.status.' + r.status | translate }}</span>
                  <strong class="code">{{ r.code }}</strong>
                </p>
                <dl class="summary">
                  <div>
                    <dt>{{ 'site.booking.when' | translate }}</dt>
                    <dd>{{ time(r.startTime) }} – {{ time(r.endTime) }}, {{ date(r.startTime) }}</dd>
                  </div>
                  <div>
                    <dt>{{ 'site.booking.steps.branch' | translate }}</dt>
                    <dd>{{ state.lang() === 'vi' ? r.branch.nameVi : r.branch.nameEn }} · {{ r.branch.phone }}</dd>
                  </div>
                  <div>
                    <dt>{{ 'site.booking.steps.services' | translate }}</dt>
                    <dd>
                      @for (s of r.services; track s.id) {
                        <div>{{ state.lang() === 'vi' ? s.nameVi : s.nameEn || s.nameVi }} × {{ s.quantity }}</div>
                      }
                    </dd>
                  </div>
                  <div>
                    <dt>{{ 'site.booking.total' | translate }}</dt>
                    <dd>{{ price(r.totalAmount) }}</dd>
                  </div>
                </dl>
                <p class="muted small">{{ 'site.lookup.changeHint' | translate: { phone: r.branch.phone } }}</p>
              </div>
            </div>
          }
        </div>
        <p class="center">
          <a [routerLink]="state.link('booking')">{{ 'site.cta.bookNow' | translate }}</a>
        </p>
      </div>
    </section>
  `,
  styles: `
    .narrow {
      max-width: 640px;
    }
    .form:hover,
    .result:hover {
      transform: none;
    }
    .code-input {
      text-transform: uppercase;
      letter-spacing: 0.1em;
    }
    .result {
      margin-top: 1.5rem;
    }
    .status {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 1rem;
    }
    .code {
      font-family: var(--site-sans);
      font-variant-numeric: lining-nums tabular-nums;
      font-size: 1.3rem;
      color: var(--site-wood);
      letter-spacing: 0.1em;
    }
    .summary div {
      display: grid;
      grid-template-columns: minmax(100px, 30%) 1fr;
      gap: 1rem;
      padding: 0.6rem 0;
      border-bottom: 1px dashed var(--site-border);
    }
    .summary dt {
      color: var(--site-muted);
      font-weight: 500;
    }
    .summary dd {
      margin: 0;
    }
    .alert-box {
      margin-top: 1.5rem;
    }
    .small {
      font-size: 0.85rem;
    }
    .center {
      margin-top: 2rem;
      text-align: center;
    }
  `,
})
export default class BookingLookupComponent {
  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);

  readonly code = signal('');
  readonly phone = signal('');
  readonly loading = signal(false);
  readonly showErrors = signal(false);
  readonly error = signal<string | null>(null);
  readonly result = signal<BookingConfirmation | null>(null);

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'booking/lookup',
        title: `${this.state.tr('site.lookup.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.lookup.intro'),
        noindex: true,
      });
    });
  }

  phoneOk(): boolean {
    return normalizeVnPhone(this.phone()) !== null;
  }

  search(): void {
    if (!this.code().trim() || !this.phoneOk()) {
      this.showErrors.set(true);
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.result.set(null);
    this.api.lookup(this.code().trim().toUpperCase(), this.phone().trim()).subscribe({
      next: r => {
        this.loading.set(false);
        this.result.set(r);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(err.status === 404 ? 'notFound' : err.status === 429 ? 'tooMany' : 'server');
      },
    });
  }

  date(iso: string): string {
    return formatDate(iso, this.state.lang(), true);
  }

  time(iso: string): string {
    return formatTime(iso);
  }

  price(v: number): string {
    return formatPrice(v, this.state.lang());
  }
}
