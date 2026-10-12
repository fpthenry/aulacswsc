import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBranch, NewBranch } from '../branch.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBranch for edit and NewBranchFormGroupInput for create.
 */
type BranchFormGroupInput = IBranch | PartialWithRequiredKeyOf<NewBranch>;

type BranchFormDefaults = Pick<NewBranch, 'id' | 'active'>;

type BranchFormGroupContent = {
  id: FormControl<IBranch['id'] | NewBranch['id']>;
  nameVi: FormControl<IBranch['nameVi']>;
  nameEn: FormControl<IBranch['nameEn']>;
  slug: FormControl<IBranch['slug']>;
  addressVi: FormControl<IBranch['addressVi']>;
  addressEn: FormControl<IBranch['addressEn']>;
  phone: FormControl<IBranch['phone']>;
  whatsapp: FormControl<IBranch['whatsapp']>;
  zalo: FormControl<IBranch['zalo']>;
  email: FormControl<IBranch['email']>;
  openTime: FormControl<IBranch['openTime']>;
  closeTime: FormControl<IBranch['closeTime']>;
  lastBookingTime: FormControl<IBranch['lastBookingTime']>;
  capacity: FormControl<IBranch['capacity']>;
  mapEmbedUrl: FormControl<IBranch['mapEmbedUrl']>;
  mapDirectionUrl: FormControl<IBranch['mapDirectionUrl']>;
  imageUrl: FormControl<IBranch['imageUrl']>;
  displayOrder: FormControl<IBranch['displayOrder']>;
  active: FormControl<IBranch['active']>;
};

export type BranchFormGroup = FormGroup<BranchFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BranchFormService {
  createBranchFormGroup(branch: BranchFormGroupInput = { id: null }): BranchFormGroup {
    const branchRawValue = {
      ...this.getFormDefaults(),
      ...branch,
    };
    return new FormGroup<BranchFormGroupContent>({
      id: new FormControl(
        { value: branchRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nameVi: new FormControl(branchRawValue.nameVi, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      nameEn: new FormControl(branchRawValue.nameEn, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      slug: new FormControl(branchRawValue.slug, {
        validators: [Validators.maxLength(180)],
      }),
      addressVi: new FormControl(branchRawValue.addressVi, {
        validators: [Validators.required, Validators.maxLength(300)],
      }),
      addressEn: new FormControl(branchRawValue.addressEn, {
        validators: [Validators.maxLength(300)],
      }),
      phone: new FormControl(branchRawValue.phone, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      whatsapp: new FormControl(branchRawValue.whatsapp, {
        validators: [Validators.maxLength(20)],
      }),
      zalo: new FormControl(branchRawValue.zalo, {
        validators: [Validators.maxLength(20)],
      }),
      email: new FormControl(branchRawValue.email, {
        validators: [Validators.maxLength(150)],
      }),
      openTime: new FormControl(branchRawValue.openTime, {
        validators: [Validators.required, Validators.pattern('^([01][0-9]|2[0-3]):[0-5][0-9]$')],
      }),
      closeTime: new FormControl(branchRawValue.closeTime, {
        validators: [Validators.required, Validators.pattern('^([01][0-9]|2[0-3]):[0-5][0-9]$')],
      }),
      lastBookingTime: new FormControl(branchRawValue.lastBookingTime, {
        validators: [Validators.required, Validators.pattern('^([01][0-9]|2[0-3]):[0-5][0-9]$')],
      }),
      capacity: new FormControl(branchRawValue.capacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      mapEmbedUrl: new FormControl(branchRawValue.mapEmbedUrl, {
        validators: [Validators.maxLength(1000)],
      }),
      mapDirectionUrl: new FormControl(branchRawValue.mapDirectionUrl, {
        validators: [Validators.maxLength(500)],
      }),
      imageUrl: new FormControl(branchRawValue.imageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      displayOrder: new FormControl(branchRawValue.displayOrder),
      active: new FormControl(branchRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getBranch(form: BranchFormGroup): IBranch | NewBranch {
    return form.getRawValue() as IBranch | NewBranch;
  }

  resetForm(form: BranchFormGroup, branch: BranchFormGroupInput): void {
    const branchRawValue = { ...this.getFormDefaults(), ...branch };
    form.reset(
      {
        ...branchRawValue,
        id: { value: branchRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BranchFormDefaults {
    return {
      id: null,
      active: false,
    };
  }
}
