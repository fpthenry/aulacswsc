import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStaff, NewStaff } from '../staff.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStaff for edit and NewStaffFormGroupInput for create.
 */
type StaffFormGroupInput = IStaff | PartialWithRequiredKeyOf<NewStaff>;

type StaffFormDefaults = Pick<NewStaff, 'id' | 'active' | 'services'>;

type StaffFormGroupContent = {
  id: FormControl<IStaff['id'] | NewStaff['id']>;
  fullName: FormControl<IStaff['fullName']>;
  phone: FormControl<IStaff['phone']>;
  position: FormControl<IStaff['position']>;
  bio: FormControl<IStaff['bio']>;
  avatarUrl: FormControl<IStaff['avatarUrl']>;
  active: FormControl<IStaff['active']>;
  user: FormControl<IStaff['user']>;
  branch: FormControl<IStaff['branch']>;
  services: FormControl<IStaff['services']>;
};

export type StaffFormGroup = FormGroup<StaffFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StaffFormService {
  createStaffFormGroup(staff: StaffFormGroupInput = { id: null }): StaffFormGroup {
    const staffRawValue = {
      ...this.getFormDefaults(),
      ...staff,
    };
    return new FormGroup<StaffFormGroupContent>({
      id: new FormControl(
        { value: staffRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(staffRawValue.fullName, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      phone: new FormControl(staffRawValue.phone, {
        validators: [Validators.maxLength(20)],
      }),
      position: new FormControl(staffRawValue.position, {
        validators: [Validators.maxLength(100)],
      }),
      bio: new FormControl(staffRawValue.bio),
      avatarUrl: new FormControl(staffRawValue.avatarUrl, {
        validators: [Validators.maxLength(500)],
      }),
      active: new FormControl(staffRawValue.active, {
        validators: [Validators.required],
      }),
      user: new FormControl(staffRawValue.user),
      branch: new FormControl(staffRawValue.branch),
      services: new FormControl(staffRawValue.services ?? []),
    });
  }

  getStaff(form: StaffFormGroup): IStaff | NewStaff {
    return form.getRawValue() as IStaff | NewStaff;
  }

  resetForm(form: StaffFormGroup, staff: StaffFormGroupInput): void {
    const staffRawValue = { ...this.getFormDefaults(), ...staff };
    form.reset(
      {
        ...staffRawValue,
        id: { value: staffRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StaffFormDefaults {
    return {
      id: null,
      active: false,
      services: [],
    };
  }
}
