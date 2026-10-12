import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BranchResolve from './route/branch-routing-resolve.service';

const branchRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/branch.component').then(m => m.BranchComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/branch-detail.component').then(m => m.BranchDetailComponent),
    resolve: {
      branch: BranchResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/branch-update.component').then(m => m.BranchUpdateComponent),
    resolve: {
      branch: BranchResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/branch-update.component').then(m => m.BranchUpdateComponent),
    resolve: {
      branch: BranchResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default branchRoute;
