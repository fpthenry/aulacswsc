import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICustomer, NewCustomer } from '../customer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomer for edit and NewCustomerFormGroupInput for create.
 */
type CustomerFormGroupInput = ICustomer | PartialWithRequiredKeyOf<NewCustomer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICustomer | NewCustomer> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type CustomerFormRawValue = FormValueOf<ICustomer>;

type NewCustomerFormRawValue = FormValueOf<NewCustomer>;

type CustomerFormDefaults = Pick<NewCustomer, 'id' | 'createdDate'>;

type CustomerFormGroupContent = {
  id: FormControl<CustomerFormRawValue['id'] | NewCustomer['id']>;
  fullName: FormControl<CustomerFormRawValue['fullName']>;
  phone: FormControl<CustomerFormRawValue['phone']>;
  email: FormControl<CustomerFormRawValue['email']>;
  gender: FormControl<CustomerFormRawValue['gender']>;
  dateOfBirth: FormControl<CustomerFormRawValue['dateOfBirth']>;
  address: FormControl<CustomerFormRawValue['address']>;
  note: FormControl<CustomerFormRawValue['note']>;
  createdDate: FormControl<CustomerFormRawValue['createdDate']>;
  user: FormControl<CustomerFormRawValue['user']>;
};

export type CustomerFormGroup = FormGroup<CustomerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerFormService {
  createCustomerFormGroup(customer: CustomerFormGroupInput = { id: null }): CustomerFormGroup {
    const customerRawValue = this.convertCustomerToCustomerRawValue({
      ...this.getFormDefaults(),
      ...customer,
    });
    return new FormGroup<CustomerFormGroupContent>({
      id: new FormControl(
        { value: customerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(customerRawValue.fullName, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      phone: new FormControl(customerRawValue.phone, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      email: new FormControl(customerRawValue.email, {
        validators: [Validators.maxLength(150)],
      }),
      gender: new FormControl(customerRawValue.gender),
      dateOfBirth: new FormControl(customerRawValue.dateOfBirth),
      address: new FormControl(customerRawValue.address, {
        validators: [Validators.maxLength(300)],
      }),
      note: new FormControl(customerRawValue.note),
      createdDate: new FormControl(customerRawValue.createdDate),
      user: new FormControl(customerRawValue.user),
    });
  }

  getCustomer(form: CustomerFormGroup): ICustomer | NewCustomer {
    return this.convertCustomerRawValueToCustomer(form.getRawValue() as CustomerFormRawValue | NewCustomerFormRawValue);
  }

  resetForm(form: CustomerFormGroup, customer: CustomerFormGroupInput): void {
    const customerRawValue = this.convertCustomerToCustomerRawValue({ ...this.getFormDefaults(), ...customer });
    form.reset(
      {
        ...customerRawValue,
        id: { value: customerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CustomerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
    };
  }

  private convertCustomerRawValueToCustomer(rawCustomer: CustomerFormRawValue | NewCustomerFormRawValue): ICustomer | NewCustomer {
    return {
      ...rawCustomer,
      createdDate: dayjs(rawCustomer.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertCustomerToCustomerRawValue(
    customer: ICustomer | (Partial<NewCustomer> & CustomerFormDefaults),
  ): CustomerFormRawValue | PartialWithRequiredKeyOf<NewCustomerFormRawValue> {
    return {
      ...customer,
      createdDate: customer.createdDate ? customer.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
