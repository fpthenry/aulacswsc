import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IServiceCategory, NewServiceCategory } from '../service-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IServiceCategory for edit and NewServiceCategoryFormGroupInput for create.
 */
type ServiceCategoryFormGroupInput = IServiceCategory | PartialWithRequiredKeyOf<NewServiceCategory>;

type ServiceCategoryFormDefaults = Pick<NewServiceCategory, 'id' | 'active'>;

type ServiceCategoryFormGroupContent = {
  id: FormControl<IServiceCategory['id'] | NewServiceCategory['id']>;
  nameVi: FormControl<IServiceCategory['nameVi']>;
  nameEn: FormControl<IServiceCategory['nameEn']>;
  slug: FormControl<IServiceCategory['slug']>;
  descriptionVi: FormControl<IServiceCategory['descriptionVi']>;
  descriptionEn: FormControl<IServiceCategory['descriptionEn']>;
  imageUrl: FormControl<IServiceCategory['imageUrl']>;
  displayOrder: FormControl<IServiceCategory['displayOrder']>;
  active: FormControl<IServiceCategory['active']>;
};

export type ServiceCategoryFormGroup = FormGroup<ServiceCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ServiceCategoryFormService {
  createServiceCategoryFormGroup(serviceCategory: ServiceCategoryFormGroupInput = { id: null }): ServiceCategoryFormGroup {
    const serviceCategoryRawValue = {
      ...this.getFormDefaults(),
      ...serviceCategory,
    };
    return new FormGroup<ServiceCategoryFormGroupContent>({
      id: new FormControl(
        { value: serviceCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nameVi: new FormControl(serviceCategoryRawValue.nameVi, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      nameEn: new FormControl(serviceCategoryRawValue.nameEn, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      slug: new FormControl(serviceCategoryRawValue.slug, {
        validators: [Validators.maxLength(180)],
      }),
      descriptionVi: new FormControl(serviceCategoryRawValue.descriptionVi, {
        validators: [Validators.maxLength(1000)],
      }),
      descriptionEn: new FormControl(serviceCategoryRawValue.descriptionEn, {
        validators: [Validators.maxLength(1000)],
      }),
      imageUrl: new FormControl(serviceCategoryRawValue.imageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      displayOrder: new FormControl(serviceCategoryRawValue.displayOrder),
      active: new FormControl(serviceCategoryRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getServiceCategory(form: ServiceCategoryFormGroup): IServiceCategory | NewServiceCategory {
    return form.getRawValue() as IServiceCategory | NewServiceCategory;
  }

  resetForm(form: ServiceCategoryFormGroup, serviceCategory: ServiceCategoryFormGroupInput): void {
    const serviceCategoryRawValue = { ...this.getFormDefaults(), ...serviceCategory };
    form.reset(
      {
        ...serviceCategoryRawValue,
        id: { value: serviceCategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ServiceCategoryFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
