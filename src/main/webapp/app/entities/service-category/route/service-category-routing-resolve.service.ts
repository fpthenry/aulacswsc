import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IServiceCategory } from '../service-category.model';
import { ServiceCategoryService } from '../service/service-category.service';

const serviceCategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IServiceCategory> => {
  const id = route.params.id;
  if (id) {
    return inject(ServiceCategoryService)
      .find(id)
      .pipe(
        mergeMap((serviceCategory: HttpResponse<IServiceCategory>) => {
          if (serviceCategory.body) {
            return of(serviceCategory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default serviceCategoryResolve;
