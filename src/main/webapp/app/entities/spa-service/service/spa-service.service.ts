import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpaService, NewSpaService } from '../spa-service.model';

export type PartialUpdateSpaService = Partial<ISpaService> & Pick<ISpaService, 'id'>;

export type EntityResponseType = HttpResponse<ISpaService>;
export type EntityArrayResponseType = HttpResponse<ISpaService[]>;

@Injectable({ providedIn: 'root' })
export class SpaServiceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/spa-services');

  create(spaService: NewSpaService): Observable<EntityResponseType> {
    return this.http.post<ISpaService>(this.resourceUrl, spaService, { observe: 'response' });
  }

  update(spaService: ISpaService): Observable<EntityResponseType> {
    return this.http.put<ISpaService>(`${this.resourceUrl}/${this.getSpaServiceIdentifier(spaService)}`, spaService, {
      observe: 'response',
    });
  }

  partialUpdate(spaService: PartialUpdateSpaService): Observable<EntityResponseType> {
    return this.http.patch<ISpaService>(`${this.resourceUrl}/${this.getSpaServiceIdentifier(spaService)}`, spaService, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpaService>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpaService[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSpaServiceIdentifier(spaService: Pick<ISpaService, 'id'>): number {
    return spaService.id;
  }

  compareSpaService(o1: Pick<ISpaService, 'id'> | null, o2: Pick<ISpaService, 'id'> | null): boolean {
    return o1 && o2 ? this.getSpaServiceIdentifier(o1) === this.getSpaServiceIdentifier(o2) : o1 === o2;
  }

  addSpaServiceToCollectionIfMissing<Type extends Pick<ISpaService, 'id'>>(
    spaServiceCollection: Type[],
    ...spaServicesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const spaServices: Type[] = spaServicesToCheck.filter(isPresent);
    if (spaServices.length > 0) {
      const spaServiceCollectionIdentifiers = spaServiceCollection.map(spaServiceItem => this.getSpaServiceIdentifier(spaServiceItem));
      const spaServicesToAdd = spaServices.filter(spaServiceItem => {
        const spaServiceIdentifier = this.getSpaServiceIdentifier(spaServiceItem);
        if (spaServiceCollectionIdentifiers.includes(spaServiceIdentifier)) {
          return false;
        }
        spaServiceCollectionIdentifiers.push(spaServiceIdentifier);
        return true;
      });
      return [...spaServicesToAdd, ...spaServiceCollection];
    }
    return spaServiceCollection;
  }
}
