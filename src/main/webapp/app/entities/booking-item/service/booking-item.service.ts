import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookingItem, NewBookingItem } from '../booking-item.model';

export type PartialUpdateBookingItem = Partial<IBookingItem> & Pick<IBookingItem, 'id'>;

export type EntityResponseType = HttpResponse<IBookingItem>;
export type EntityArrayResponseType = HttpResponse<IBookingItem[]>;

@Injectable({ providedIn: 'root' })
export class BookingItemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/booking-items');

  create(bookingItem: NewBookingItem): Observable<EntityResponseType> {
    return this.http.post<IBookingItem>(this.resourceUrl, bookingItem, { observe: 'response' });
  }

  update(bookingItem: IBookingItem): Observable<EntityResponseType> {
    return this.http.put<IBookingItem>(`${this.resourceUrl}/${this.getBookingItemIdentifier(bookingItem)}`, bookingItem, {
      observe: 'response',
    });
  }

  partialUpdate(bookingItem: PartialUpdateBookingItem): Observable<EntityResponseType> {
    return this.http.patch<IBookingItem>(`${this.resourceUrl}/${this.getBookingItemIdentifier(bookingItem)}`, bookingItem, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBookingItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBookingItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookingItemIdentifier(bookingItem: Pick<IBookingItem, 'id'>): number {
    return bookingItem.id;
  }

  compareBookingItem(o1: Pick<IBookingItem, 'id'> | null, o2: Pick<IBookingItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookingItemIdentifier(o1) === this.getBookingItemIdentifier(o2) : o1 === o2;
  }

  addBookingItemToCollectionIfMissing<Type extends Pick<IBookingItem, 'id'>>(
    bookingItemCollection: Type[],
    ...bookingItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookingItems: Type[] = bookingItemsToCheck.filter(isPresent);
    if (bookingItems.length > 0) {
      const bookingItemCollectionIdentifiers = bookingItemCollection.map(bookingItemItem => this.getBookingItemIdentifier(bookingItemItem));
      const bookingItemsToAdd = bookingItems.filter(bookingItemItem => {
        const bookingItemIdentifier = this.getBookingItemIdentifier(bookingItemItem);
        if (bookingItemCollectionIdentifiers.includes(bookingItemIdentifier)) {
          return false;
        }
        bookingItemCollectionIdentifiers.push(bookingItemIdentifier);
        return true;
      });
      return [...bookingItemsToAdd, ...bookingItemCollection];
    }
    return bookingItemCollection;
  }
}
