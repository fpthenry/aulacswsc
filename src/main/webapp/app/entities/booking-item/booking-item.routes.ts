import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BookingItemResolve from './route/booking-item-routing-resolve.service';

const bookingItemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/booking-item.component').then(m => m.BookingItemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/booking-item-detail.component').then(m => m.BookingItemDetailComponent),
    resolve: {
      bookingItem: BookingItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/booking-item-update.component').then(m => m.BookingItemUpdateComponent),
    resolve: {
      bookingItem: BookingItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/booking-item-update.component').then(m => m.BookingItemUpdateComponent),
    resolve: {
      bookingItem: BookingItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookingItemRoute;
