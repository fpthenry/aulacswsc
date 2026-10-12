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
  {
    // trang chủ khu quản trị (trước đây là '')
    path: 'dashboard',
    loadComponent: () => import('./home/home.component'),
    title: 'home.title',
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
