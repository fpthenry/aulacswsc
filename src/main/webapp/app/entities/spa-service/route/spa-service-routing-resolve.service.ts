import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpaService } from '../spa-service.model';
import { SpaServiceService } from '../service/spa-service.service';

const spaServiceResolve = (route: ActivatedRouteSnapshot): Observable<null | ISpaService> => {
  const id = route.params.id;
  if (id) {
    return inject(SpaServiceService)
      .find(id)
      .pipe(
        mergeMap((spaService: HttpResponse<ISpaService>) => {
          if (spaService.body) {
            return of(spaService.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default spaServiceResolve;
