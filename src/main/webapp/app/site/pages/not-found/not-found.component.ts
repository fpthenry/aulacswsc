import { Component, effect, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteStateService } from '../../core/site-state.service';

@Component({
  selector: 'site-not-found',
  imports: [PageHeroComponent, RouterLink, TranslateModule],
  template: `
    <site-page-hero>
      <p class="eyebrow">404</p>
      <h1>{{ 'site.notFound.title' | translate }}</h1>
      <p class="lead">{{ 'site.notFound.text' | translate }}</p>
      <a class="btn-site btn-primary" [routerLink]="state.link()">{{ 'site.nav.home' | translate }}</a>
    </site-page-hero>
  `,
})
export default class NotFoundComponent {
  readonly state = inject(SiteStateService);
  private readonly seo = inject(SeoService);

  constructor() {
    effect(() => {
      this.seo.set({ lang: this.state.lang(), path: '', title: this.state.tr('site.notFound.title'), noindex: true });
    });
  }
}
