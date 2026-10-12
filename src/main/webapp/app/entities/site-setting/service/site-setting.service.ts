import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISiteSetting, NewSiteSetting } from '../site-setting.model';

export type PartialUpdateSiteSetting = Partial<ISiteSetting> & Pick<ISiteSetting, 'id'>;

export type EntityResponseType = HttpResponse<ISiteSetting>;
export type EntityArrayResponseType = HttpResponse<ISiteSetting[]>;

@Injectable({ providedIn: 'root' })
export class SiteSettingService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/site-settings');

  create(siteSetting: NewSiteSetting): Observable<EntityResponseType> {
    return this.http.post<ISiteSetting>(this.resourceUrl, siteSetting, { observe: 'response' });
  }

  update(siteSetting: ISiteSetting): Observable<EntityResponseType> {
    return this.http.put<ISiteSetting>(`${this.resourceUrl}/${this.getSiteSettingIdentifier(siteSetting)}`, siteSetting, {
      observe: 'response',
    });
  }

  partialUpdate(siteSetting: PartialUpdateSiteSetting): Observable<EntityResponseType> {
    return this.http.patch<ISiteSetting>(`${this.resourceUrl}/${this.getSiteSettingIdentifier(siteSetting)}`, siteSetting, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISiteSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISiteSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSiteSettingIdentifier(siteSetting: Pick<ISiteSetting, 'id'>): number {
    return siteSetting.id;
  }

  compareSiteSetting(o1: Pick<ISiteSetting, 'id'> | null, o2: Pick<ISiteSetting, 'id'> | null): boolean {
    return o1 && o2 ? this.getSiteSettingIdentifier(o1) === this.getSiteSettingIdentifier(o2) : o1 === o2;
  }

  addSiteSettingToCollectionIfMissing<Type extends Pick<ISiteSetting, 'id'>>(
    siteSettingCollection: Type[],
    ...siteSettingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const siteSettings: Type[] = siteSettingsToCheck.filter(isPresent);
    if (siteSettings.length > 0) {
      const siteSettingCollectionIdentifiers = siteSettingCollection.map(siteSettingItem => this.getSiteSettingIdentifier(siteSettingItem));
      const siteSettingsToAdd = siteSettings.filter(siteSettingItem => {
        const siteSettingIdentifier = this.getSiteSettingIdentifier(siteSettingItem);
        if (siteSettingCollectionIdentifiers.includes(siteSettingIdentifier)) {
          return false;
        }
        siteSettingCollectionIdentifiers.push(siteSettingIdentifier);
        return true;
      });
      return [...siteSettingsToAdd, ...siteSettingCollection];
    }
    return siteSettingCollection;
  }
}
