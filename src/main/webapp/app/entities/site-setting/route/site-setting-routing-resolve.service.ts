import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISiteSetting } from '../site-setting.model';
import { SiteSettingService } from '../service/site-setting.service';

const siteSettingResolve = (route: ActivatedRouteSnapshot): Observable<null | ISiteSetting> => {
  const id = route.params.id;
  if (id) {
    return inject(SiteSettingService)
      .find(id)
      .pipe(
        mergeMap((siteSetting: HttpResponse<ISiteSetting>) => {
          if (siteSetting.body) {
            return of(siteSetting.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default siteSettingResolve;
