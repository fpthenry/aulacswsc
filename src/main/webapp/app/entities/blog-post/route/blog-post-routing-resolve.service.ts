import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBlogPost } from '../blog-post.model';
import { BlogPostService } from '../service/blog-post.service';

const blogPostResolve = (route: ActivatedRouteSnapshot): Observable<null | IBlogPost> => {
  const id = route.params.id;
  if (id) {
    return inject(BlogPostService)
      .find(id)
      .pipe(
        mergeMap((blogPost: HttpResponse<IBlogPost>) => {
          if (blogPost.body) {
            return of(blogPost.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default blogPostResolve;
