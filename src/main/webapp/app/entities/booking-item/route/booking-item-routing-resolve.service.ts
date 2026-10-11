import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookingItem } from '../booking-item.model';
import { BookingItemService } from '../service/booking-item.service';

const bookingItemResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookingItem> => {
  const id = route.params.id;
  if (id) {
    return inject(BookingItemService)
      .find(id)
      .pipe(
        mergeMap((bookingItem: HttpResponse<IBookingItem>) => {
          if (bookingItem.body) {
            return of(bookingItem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default bookingItemResolve;
