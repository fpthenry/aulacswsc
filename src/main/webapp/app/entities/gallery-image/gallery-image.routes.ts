import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import GalleryImageResolve from './route/gallery-image-routing-resolve.service';

const galleryImageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/gallery-image.component').then(m => m.GalleryImageComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/gallery-image-detail.component').then(m => m.GalleryImageDetailComponent),
    resolve: {
      galleryImage: GalleryImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/gallery-image-update.component').then(m => m.GalleryImageUpdateComponent),
    resolve: {
      galleryImage: GalleryImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/gallery-image-update.component').then(m => m.GalleryImageUpdateComponent),
    resolve: {
      galleryImage: GalleryImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default galleryImageRoute;
