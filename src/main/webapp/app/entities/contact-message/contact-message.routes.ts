import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ContactMessageResolve from './route/contact-message-routing-resolve.service';

const contactMessageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/contact-message.component').then(m => m.ContactMessageComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/contact-message-detail.component').then(m => m.ContactMessageDetailComponent),
    resolve: {
      contactMessage: ContactMessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/contact-message-update.component').then(m => m.ContactMessageUpdateComponent),
    resolve: {
      contactMessage: ContactMessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/contact-message-update.component').then(m => m.ContactMessageUpdateComponent),
    resolve: {
      contactMessage: ContactMessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default contactMessageRoute;
