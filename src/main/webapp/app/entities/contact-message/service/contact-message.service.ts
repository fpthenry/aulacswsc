import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IContactMessage, NewContactMessage } from '../contact-message.model';

export type PartialUpdateContactMessage = Partial<IContactMessage> & Pick<IContactMessage, 'id'>;

type RestOf<T extends IContactMessage | NewContactMessage> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

export type RestContactMessage = RestOf<IContactMessage>;

export type NewRestContactMessage = RestOf<NewContactMessage>;

export type PartialUpdateRestContactMessage = RestOf<PartialUpdateContactMessage>;

export type EntityResponseType = HttpResponse<IContactMessage>;
export type EntityArrayResponseType = HttpResponse<IContactMessage[]>;

@Injectable({ providedIn: 'root' })
export class ContactMessageService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/contact-messages');

  create(contactMessage: NewContactMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(contactMessage);
    return this.http
      .post<RestContactMessage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(contactMessage: IContactMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(contactMessage);
    return this.http
      .put<RestContactMessage>(`${this.resourceUrl}/${this.getContactMessageIdentifier(contactMessage)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(contactMessage: PartialUpdateContactMessage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(contactMessage);
    return this.http
      .patch<RestContactMessage>(`${this.resourceUrl}/${this.getContactMessageIdentifier(contactMessage)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestContactMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestContactMessage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getContactMessageIdentifier(contactMessage: Pick<IContactMessage, 'id'>): number {
    return contactMessage.id;
  }

  compareContactMessage(o1: Pick<IContactMessage, 'id'> | null, o2: Pick<IContactMessage, 'id'> | null): boolean {
    return o1 && o2 ? this.getContactMessageIdentifier(o1) === this.getContactMessageIdentifier(o2) : o1 === o2;
  }

  addContactMessageToCollectionIfMissing<Type extends Pick<IContactMessage, 'id'>>(
    contactMessageCollection: Type[],
    ...contactMessagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const contactMessages: Type[] = contactMessagesToCheck.filter(isPresent);
    if (contactMessages.length > 0) {
      const contactMessageCollectionIdentifiers = contactMessageCollection.map(contactMessageItem =>
        this.getContactMessageIdentifier(contactMessageItem),
      );
      const contactMessagesToAdd = contactMessages.filter(contactMessageItem => {
        const contactMessageIdentifier = this.getContactMessageIdentifier(contactMessageItem);
        if (contactMessageCollectionIdentifiers.includes(contactMessageIdentifier)) {
          return false;
        }
        contactMessageCollectionIdentifiers.push(contactMessageIdentifier);
        return true;
      });
      return [...contactMessagesToAdd, ...contactMessageCollection];
    }
    return contactMessageCollection;
  }

  protected convertDateFromClient<T extends IContactMessage | NewContactMessage | PartialUpdateContactMessage>(
    contactMessage: T,
  ): RestOf<T> {
    return {
      ...contactMessage,
      createdDate: contactMessage.createdDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restContactMessage: RestContactMessage): IContactMessage {
    return {
      ...restContactMessage,
      createdDate: restContactMessage.createdDate ? dayjs(restContactMessage.createdDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestContactMessage>): HttpResponse<IContactMessage> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestContactMessage[]>): HttpResponse<IContactMessage[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
