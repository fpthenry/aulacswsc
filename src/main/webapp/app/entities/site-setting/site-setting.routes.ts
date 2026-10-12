import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SiteSettingResolve from './route/site-setting-routing-resolve.service';

const siteSettingRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/site-setting.component').then(m => m.SiteSettingComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/site-setting-detail.component').then(m => m.SiteSettingDetailComponent),
    resolve: {
      siteSetting: SiteSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/site-setting-update.component').then(m => m.SiteSettingUpdateComponent),
    resolve: {
      siteSetting: SiteSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/site-setting-update.component').then(m => m.SiteSettingUpdateComponent),
    resolve: {
      siteSetting: SiteSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default siteSettingRoute;
