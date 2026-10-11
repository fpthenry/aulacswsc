import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IContactMessage } from '../contact-message.model';
import { ContactMessageService } from '../service/contact-message.service';

const contactMessageResolve = (route: ActivatedRouteSnapshot): Observable<null | IContactMessage> => {
  const id = route.params.id;
  if (id) {
    return inject(ContactMessageService)
      .find(id)
      .pipe(
        mergeMap((contactMessage: HttpResponse<IContactMessage>) => {
          if (contactMessage.body) {
            return of(contactMessage.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default contactMessageResolve;
