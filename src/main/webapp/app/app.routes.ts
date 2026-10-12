import { Routes } from '@angular/router';
import { siteLangMatch, preferredSiteLang } from './site/core/site-lang';

import { Authority } from 'app/config/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    // trang gốc → website khách theo ngôn ngữ ưa thích (mặc định /vi)
    path: '',
    pathMatch: 'full',
    redirectTo: () => `/${preferredSiteLang()}`,
  },
  {
    // website công khai: /vi/... và /en/...
    path: ':lang',
    canMatch: [siteLangMatch],
    loadChildren: () => import('./site/site.routes'),
  },
  // ===== trang quản trị (khung: app/backoffice) =====
  {
    path: 'dashboard',
    data: { authorities: [Authority.ADMIN, 'ROLE_STAFF'] },
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./backoffice/dashboard/dashboard.component'),
    title: 'global.title',
  },
  {
    path: 'booking-board',
    data: { authorities: [Authority.ADMIN, 'ROLE_STAFF'] },
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./backoffice/bookings-board/bookings-board.component'),
    title: 'global.title',
  },
  {
    path: 'cai-dat-website',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./backoffice/site-setting-shortcut/site-setting-shortcut.component'),
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar.component'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    title: 'login.title',
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  ...errorRoute,
];

export default routes;
