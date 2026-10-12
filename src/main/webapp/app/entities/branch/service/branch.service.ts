import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBranch, NewBranch } from '../branch.model';

export type PartialUpdateBranch = Partial<IBranch> & Pick<IBranch, 'id'>;

export type EntityResponseType = HttpResponse<IBranch>;
export type EntityArrayResponseType = HttpResponse<IBranch[]>;

@Injectable({ providedIn: 'root' })
export class BranchService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/branches');

  create(branch: NewBranch): Observable<EntityResponseType> {
    return this.http.post<IBranch>(this.resourceUrl, branch, { observe: 'response' });
  }

  update(branch: IBranch): Observable<EntityResponseType> {
    return this.http.put<IBranch>(`${this.resourceUrl}/${this.getBranchIdentifier(branch)}`, branch, { observe: 'response' });
  }

  partialUpdate(branch: PartialUpdateBranch): Observable<EntityResponseType> {
    return this.http.patch<IBranch>(`${this.resourceUrl}/${this.getBranchIdentifier(branch)}`, branch, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBranch>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBranch[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBranchIdentifier(branch: Pick<IBranch, 'id'>): number {
    return branch.id;
  }

  compareBranch(o1: Pick<IBranch, 'id'> | null, o2: Pick<IBranch, 'id'> | null): boolean {
    return o1 && o2 ? this.getBranchIdentifier(o1) === this.getBranchIdentifier(o2) : o1 === o2;
  }

  addBranchToCollectionIfMissing<Type extends Pick<IBranch, 'id'>>(
    branchCollection: Type[],
    ...branchesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const branches: Type[] = branchesToCheck.filter(isPresent);
    if (branches.length > 0) {
      const branchCollectionIdentifiers = branchCollection.map(branchItem => this.getBranchIdentifier(branchItem));
      const branchesToAdd = branches.filter(branchItem => {
        const branchIdentifier = this.getBranchIdentifier(branchItem);
        if (branchCollectionIdentifiers.includes(branchIdentifier)) {
          return false;
        }
        branchCollectionIdentifiers.push(branchIdentifier);
        return true;
      });
      return [...branchesToAdd, ...branchCollection];
    }
    return branchCollection;
  }
}
