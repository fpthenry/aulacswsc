import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISpaService, NewSpaService } from '../spa-service.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISpaService for edit and NewSpaServiceFormGroupInput for create.
 */
type SpaServiceFormGroupInput = ISpaService | PartialWithRequiredKeyOf<NewSpaService>;

type SpaServiceFormDefaults = Pick<NewSpaService, 'id' | 'featured' | 'active' | 'staffs'>;

type SpaServiceFormGroupContent = {
  id: FormControl<ISpaService['id'] | NewSpaService['id']>;
  name: FormControl<ISpaService['name']>;
  slug: FormControl<ISpaService['slug']>;
  shortDescription: FormControl<ISpaService['shortDescription']>;
  description: FormControl<ISpaService['description']>;
  price: FormControl<ISpaService['price']>;
  salePrice: FormControl<ISpaService['salePrice']>;
  durationMinutes: FormControl<ISpaService['durationMinutes']>;
  imageUrl: FormControl<ISpaService['imageUrl']>;
  featured: FormControl<ISpaService['featured']>;
  active: FormControl<ISpaService['active']>;
  category: FormControl<ISpaService['category']>;
  staffs: FormControl<ISpaService['staffs']>;
};

export type SpaServiceFormGroup = FormGroup<SpaServiceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SpaServiceFormService {
  createSpaServiceFormGroup(spaService: SpaServiceFormGroupInput = { id: null }): SpaServiceFormGroup {
    const spaServiceRawValue = {
      ...this.getFormDefaults(),
      ...spaService,
    };
    return new FormGroup<SpaServiceFormGroupContent>({
      id: new FormControl(
        { value: spaServiceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(spaServiceRawValue.name, {
        validators: [Validators.required, Validators.maxLength(200)],
      }),
      slug: new FormControl(spaServiceRawValue.slug, {
        validators: [Validators.required, Validators.maxLength(220)],
      }),
      shortDescription: new FormControl(spaServiceRawValue.shortDescription, {
        validators: [Validators.maxLength(500)],
      }),
      description: new FormControl(spaServiceRawValue.description),
      price: new FormControl(spaServiceRawValue.price, {
        validators: [Validators.required, Validators.min(0)],
      }),
      salePrice: new FormControl(spaServiceRawValue.salePrice, {
        validators: [Validators.min(0)],
      }),
      durationMinutes: new FormControl(spaServiceRawValue.durationMinutes, {
        validators: [Validators.required, Validators.min(5)],
      }),
      imageUrl: new FormControl(spaServiceRawValue.imageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      featured: new FormControl(spaServiceRawValue.featured),
      active: new FormControl(spaServiceRawValue.active, {
        validators: [Validators.required],
      }),
      category: new FormControl(spaServiceRawValue.category, {
        validators: [Validators.required],
      }),
      staffs: new FormControl(spaServiceRawValue.staffs ?? []),
    });
  }

  getSpaService(form: SpaServiceFormGroup): ISpaService | NewSpaService {
    return form.getRawValue() as ISpaService | NewSpaService;
  }

  resetForm(form: SpaServiceFormGroup, spaService: SpaServiceFormGroupInput): void {
    const spaServiceRawValue = { ...this.getFormDefaults(), ...spaService };
    form.reset(
      {
        ...spaServiceRawValue,
        id: { value: spaServiceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SpaServiceFormDefaults {
    return {
      id: null,
      featured: false,
      active: false,
      staffs: [],
    };
  }
}
