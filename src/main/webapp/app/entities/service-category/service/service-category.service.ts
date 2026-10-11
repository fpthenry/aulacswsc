import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IServiceCategory, NewServiceCategory } from '../service-category.model';

export type PartialUpdateServiceCategory = Partial<IServiceCategory> & Pick<IServiceCategory, 'id'>;

export type EntityResponseType = HttpResponse<IServiceCategory>;
export type EntityArrayResponseType = HttpResponse<IServiceCategory[]>;

@Injectable({ providedIn: 'root' })
export class ServiceCategoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/service-categories');

  create(serviceCategory: NewServiceCategory): Observable<EntityResponseType> {
    return this.http.post<IServiceCategory>(this.resourceUrl, serviceCategory, { observe: 'response' });
  }

  update(serviceCategory: IServiceCategory): Observable<EntityResponseType> {
    return this.http.put<IServiceCategory>(`${this.resourceUrl}/${this.getServiceCategoryIdentifier(serviceCategory)}`, serviceCategory, {
      observe: 'response',
    });
  }

  partialUpdate(serviceCategory: PartialUpdateServiceCategory): Observable<EntityResponseType> {
    return this.http.patch<IServiceCategory>(`${this.resourceUrl}/${this.getServiceCategoryIdentifier(serviceCategory)}`, serviceCategory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IServiceCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IServiceCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getServiceCategoryIdentifier(serviceCategory: Pick<IServiceCategory, 'id'>): number {
    return serviceCategory.id;
  }

  compareServiceCategory(o1: Pick<IServiceCategory, 'id'> | null, o2: Pick<IServiceCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getServiceCategoryIdentifier(o1) === this.getServiceCategoryIdentifier(o2) : o1 === o2;
  }

  addServiceCategoryToCollectionIfMissing<Type extends Pick<IServiceCategory, 'id'>>(
    serviceCategoryCollection: Type[],
    ...serviceCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const serviceCategories: Type[] = serviceCategoriesToCheck.filter(isPresent);
    if (serviceCategories.length > 0) {
      const serviceCategoryCollectionIdentifiers = serviceCategoryCollection.map(serviceCategoryItem =>
        this.getServiceCategoryIdentifier(serviceCategoryItem),
      );
      const serviceCategoriesToAdd = serviceCategories.filter(serviceCategoryItem => {
        const serviceCategoryIdentifier = this.getServiceCategoryIdentifier(serviceCategoryItem);
        if (serviceCategoryCollectionIdentifiers.includes(serviceCategoryIdentifier)) {
          return false;
        }
        serviceCategoryCollectionIdentifiers.push(serviceCategoryIdentifier);
        return true;
      });
      return [...serviceCategoriesToAdd, ...serviceCategoryCollection];
    }
    return serviceCategoryCollection;
  }
}
