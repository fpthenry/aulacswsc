import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGalleryImage, NewGalleryImage } from '../gallery-image.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGalleryImage for edit and NewGalleryImageFormGroupInput for create.
 */
type GalleryImageFormGroupInput = IGalleryImage | PartialWithRequiredKeyOf<NewGalleryImage>;

type GalleryImageFormDefaults = Pick<NewGalleryImage, 'id' | 'active'>;

type GalleryImageFormGroupContent = {
  id: FormControl<IGalleryImage['id'] | NewGalleryImage['id']>;
  imageUrl: FormControl<IGalleryImage['imageUrl']>;
  captionVi: FormControl<IGalleryImage['captionVi']>;
  captionEn: FormControl<IGalleryImage['captionEn']>;
  altVi: FormControl<IGalleryImage['altVi']>;
  altEn: FormControl<IGalleryImage['altEn']>;
  width: FormControl<IGalleryImage['width']>;
  height: FormControl<IGalleryImage['height']>;
  displayOrder: FormControl<IGalleryImage['displayOrder']>;
  active: FormControl<IGalleryImage['active']>;
  branch: FormControl<IGalleryImage['branch']>;
};

export type GalleryImageFormGroup = FormGroup<GalleryImageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GalleryImageFormService {
  createGalleryImageFormGroup(galleryImage: GalleryImageFormGroupInput = { id: null }): GalleryImageFormGroup {
    const galleryImageRawValue = {
      ...this.getFormDefaults(),
      ...galleryImage,
    };
    return new FormGroup<GalleryImageFormGroupContent>({
      id: new FormControl(
        { value: galleryImageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      imageUrl: new FormControl(galleryImageRawValue.imageUrl, {
        validators: [Validators.required, Validators.maxLength(500)],
      }),
      captionVi: new FormControl(galleryImageRawValue.captionVi, {
        validators: [Validators.maxLength(300)],
      }),
      captionEn: new FormControl(galleryImageRawValue.captionEn, {
        validators: [Validators.maxLength(300)],
      }),
      altVi: new FormControl(galleryImageRawValue.altVi, {
        validators: [Validators.required, Validators.maxLength(300)],
      }),
      altEn: new FormControl(galleryImageRawValue.altEn, {
        validators: [Validators.maxLength(300)],
      }),
      width: new FormControl(galleryImageRawValue.width),
      height: new FormControl(galleryImageRawValue.height),
      displayOrder: new FormControl(galleryImageRawValue.displayOrder),
      active: new FormControl(galleryImageRawValue.active, {
        validators: [Validators.required],
      }),
      branch: new FormControl(galleryImageRawValue.branch),
    });
  }

  getGalleryImage(form: GalleryImageFormGroup): IGalleryImage | NewGalleryImage {
    return form.getRawValue() as IGalleryImage | NewGalleryImage;
  }

  resetForm(form: GalleryImageFormGroup, galleryImage: GalleryImageFormGroupInput): void {
    const galleryImageRawValue = { ...this.getFormDefaults(), ...galleryImage };
    form.reset(
      {
        ...galleryImageRawValue,
        id: { value: galleryImageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GalleryImageFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
