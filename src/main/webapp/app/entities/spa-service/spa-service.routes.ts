import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SpaServiceResolve from './route/spa-service-routing-resolve.service';

const spaServiceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/spa-service.component').then(m => m.SpaServiceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/spa-service-detail.component').then(m => m.SpaServiceDetailComponent),
    resolve: {
      spaService: SpaServiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/spa-service-update.component').then(m => m.SpaServiceUpdateComponent),
    resolve: {
      spaService: SpaServiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/spa-service-update.component').then(m => m.SpaServiceUpdateComponent),
    resolve: {
      spaService: SpaServiceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default spaServiceRoute;
