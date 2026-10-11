import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ServiceCategoryResolve from './route/service-category-routing-resolve.service';

const serviceCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/service-category.component').then(m => m.ServiceCategoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/service-category-detail.component').then(m => m.ServiceCategoryDetailComponent),
    resolve: {
      serviceCategory: ServiceCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/service-category-update.component').then(m => m.ServiceCategoryUpdateComponent),
    resolve: {
      serviceCategory: ServiceCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/service-category-update.component').then(m => m.ServiceCategoryUpdateComponent),
    resolve: {
      serviceCategory: ServiceCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default serviceCategoryRoute;
