import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGalleryImage, NewGalleryImage } from '../gallery-image.model';

export type PartialUpdateGalleryImage = Partial<IGalleryImage> & Pick<IGalleryImage, 'id'>;

export type EntityResponseType = HttpResponse<IGalleryImage>;
export type EntityArrayResponseType = HttpResponse<IGalleryImage[]>;

@Injectable({ providedIn: 'root' })
export class GalleryImageService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/gallery-images');

  create(galleryImage: NewGalleryImage): Observable<EntityResponseType> {
    return this.http.post<IGalleryImage>(this.resourceUrl, galleryImage, { observe: 'response' });
  }

  update(galleryImage: IGalleryImage): Observable<EntityResponseType> {
    return this.http.put<IGalleryImage>(`${this.resourceUrl}/${this.getGalleryImageIdentifier(galleryImage)}`, galleryImage, {
      observe: 'response',
    });
  }

  partialUpdate(galleryImage: PartialUpdateGalleryImage): Observable<EntityResponseType> {
    return this.http.patch<IGalleryImage>(`${this.resourceUrl}/${this.getGalleryImageIdentifier(galleryImage)}`, galleryImage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGalleryImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGalleryImage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGalleryImageIdentifier(galleryImage: Pick<IGalleryImage, 'id'>): number {
    return galleryImage.id;
  }

  compareGalleryImage(o1: Pick<IGalleryImage, 'id'> | null, o2: Pick<IGalleryImage, 'id'> | null): boolean {
    return o1 && o2 ? this.getGalleryImageIdentifier(o1) === this.getGalleryImageIdentifier(o2) : o1 === o2;
  }

  addGalleryImageToCollectionIfMissing<Type extends Pick<IGalleryImage, 'id'>>(
    galleryImageCollection: Type[],
    ...galleryImagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const galleryImages: Type[] = galleryImagesToCheck.filter(isPresent);
    if (galleryImages.length > 0) {
      const galleryImageCollectionIdentifiers = galleryImageCollection.map(galleryImageItem =>
        this.getGalleryImageIdentifier(galleryImageItem),
      );
      const galleryImagesToAdd = galleryImages.filter(galleryImageItem => {
        const galleryImageIdentifier = this.getGalleryImageIdentifier(galleryImageItem);
        if (galleryImageCollectionIdentifiers.includes(galleryImageIdentifier)) {
          return false;
        }
        galleryImageCollectionIdentifiers.push(galleryImageIdentifier);
        return true;
      });
      return [...galleryImagesToAdd, ...galleryImageCollection];
    }
    return galleryImageCollection;
  }
}
