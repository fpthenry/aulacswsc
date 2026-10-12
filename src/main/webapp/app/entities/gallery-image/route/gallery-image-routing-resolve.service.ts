import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGalleryImage } from '../gallery-image.model';
import { GalleryImageService } from '../service/gallery-image.service';

const galleryImageResolve = (route: ActivatedRouteSnapshot): Observable<null | IGalleryImage> => {
  const id = route.params.id;
  if (id) {
    return inject(GalleryImageService)
      .find(id)
      .pipe(
        mergeMap((galleryImage: HttpResponse<IGalleryImage>) => {
          if (galleryImage.body) {
            return of(galleryImage.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default galleryImageResolve;
