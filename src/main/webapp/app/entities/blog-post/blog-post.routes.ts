import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BlogPostResolve from './route/blog-post-routing-resolve.service';

const blogPostRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/blog-post.component').then(m => m.BlogPostComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/blog-post-detail.component').then(m => m.BlogPostDetailComponent),
    resolve: {
      blogPost: BlogPostResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/blog-post-update.component').then(m => m.BlogPostUpdateComponent),
    resolve: {
      blogPost: BlogPostResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/blog-post-update.component').then(m => m.BlogPostUpdateComponent),
    resolve: {
      blogPost: BlogPostResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default blogPostRoute;
