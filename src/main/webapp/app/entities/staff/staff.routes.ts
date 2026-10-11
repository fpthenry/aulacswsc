import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import StaffResolve from './route/staff-routing-resolve.service';

const staffRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/staff.component').then(m => m.StaffComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/staff-detail.component').then(m => m.StaffDetailComponent),
    resolve: {
      staff: StaffResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/staff-update.component').then(m => m.StaffUpdateComponent),
    resolve: {
      staff: StaffResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/staff-update.component').then(m => m.StaffUpdateComponent),
    resolve: {
      staff: StaffResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default staffRoute;
