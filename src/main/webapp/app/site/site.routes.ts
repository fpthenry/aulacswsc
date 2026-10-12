import { inject } from '@angular/core';
import { ResolveFn, Routes } from '@angular/router';
import { catchError, of } from 'rxjs';

import { SiteStateService } from './core/site-state.service';
import { SiteBootstrap } from './core/site.models';

/** Tải cài đặt + dịch vụ trước khi hiển thị trang, để nội dung và SEO có ngay. */
const siteDataResolver: ResolveFn<SiteBootstrap | null> = () =>
  inject(SiteStateService)
    .load()
    .pipe(catchError(() => of(null)));

/**
 * Website khách, gắn dưới /:lang (vi | en) trong app.routes.ts.
 */
const siteRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/site-layout.component'),
    resolve: { site: siteDataResolver },
    children: [
      { path: '', loadComponent: () => import('./pages/home/home.component') },
      { path: 'services', loadComponent: () => import('./pages/services/services.component') },
      { path: 'services/:slug', loadComponent: () => import('./pages/service-detail/service-detail.component') },
      { path: 'branches', loadComponent: () => import('./pages/branches/branches.component') },
      { path: 'reviews', loadComponent: () => import('./pages/reviews/reviews.component') },
      { path: 'gallery', loadComponent: () => import('./pages/gallery/gallery.component') },
      { path: 'blog', loadComponent: () => import('./pages/blog/blog.component') },
      { path: 'blog/:slug', loadComponent: () => import('./pages/blog-detail/blog-detail.component') },
      { path: 'booking', loadComponent: () => import('./pages/booking/booking.component') },
      { path: 'booking/lookup', loadComponent: () => import('./pages/booking-lookup/booking-lookup.component') },
      { path: 'policy', loadComponent: () => import('./pages/policy/policy.component') },
      { path: '**', loadComponent: () => import('./pages/not-found/not-found.component') },
    ],
  },
];

export default siteRoutes;
