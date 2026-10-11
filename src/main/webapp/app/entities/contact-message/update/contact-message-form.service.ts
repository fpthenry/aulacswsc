import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IContactMessage, NewContactMessage } from '../contact-message.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IContactMessage for edit and NewContactMessageFormGroupInput for create.
 */
type ContactMessageFormGroupInput = IContactMessage | PartialWithRequiredKeyOf<NewContactMessage>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IContactMessage | NewContactMessage> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type ContactMessageFormRawValue = FormValueOf<IContactMessage>;

type NewContactMessageFormRawValue = FormValueOf<NewContactMessage>;

type ContactMessageFormDefaults = Pick<NewContactMessage, 'id' | 'handled' | 'createdDate'>;

type ContactMessageFormGroupContent = {
  id: FormControl<ContactMessageFormRawValue['id'] | NewContactMessage['id']>;
  fullName: FormControl<ContactMessageFormRawValue['fullName']>;
  phone: FormControl<ContactMessageFormRawValue['phone']>;
  email: FormControl<ContactMessageFormRawValue['email']>;
  message: FormControl<ContactMessageFormRawValue['message']>;
  handled: FormControl<ContactMessageFormRawValue['handled']>;
  createdDate: FormControl<ContactMessageFormRawValue['createdDate']>;
};

export type ContactMessageFormGroup = FormGroup<ContactMessageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ContactMessageFormService {
  createContactMessageFormGroup(contactMessage: ContactMessageFormGroupInput = { id: null }): ContactMessageFormGroup {
    const contactMessageRawValue = this.convertContactMessageToContactMessageRawValue({
      ...this.getFormDefaults(),
      ...contactMessage,
    });
    return new FormGroup<ContactMessageFormGroupContent>({
      id: new FormControl(
        { value: contactMessageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(contactMessageRawValue.fullName, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      phone: new FormControl(contactMessageRawValue.phone, {
        validators: [Validators.maxLength(20)],
      }),
      email: new FormControl(contactMessageRawValue.email, {
        validators: [Validators.maxLength(150)],
      }),
      message: new FormControl(contactMessageRawValue.message, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      handled: new FormControl(contactMessageRawValue.handled),
      createdDate: new FormControl(contactMessageRawValue.createdDate),
    });
  }

  getContactMessage(form: ContactMessageFormGroup): IContactMessage | NewContactMessage {
    return this.convertContactMessageRawValueToContactMessage(
      form.getRawValue() as ContactMessageFormRawValue | NewContactMessageFormRawValue,
    );
  }

  resetForm(form: ContactMessageFormGroup, contactMessage: ContactMessageFormGroupInput): void {
    const contactMessageRawValue = this.convertContactMessageToContactMessageRawValue({ ...this.getFormDefaults(), ...contactMessage });
    form.reset(
      {
        ...contactMessageRawValue,
        id: { value: contactMessageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ContactMessageFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      handled: false,
      createdDate: currentTime,
    };
  }

  private convertContactMessageRawValueToContactMessage(
    rawContactMessage: ContactMessageFormRawValue | NewContactMessageFormRawValue,
  ): IContactMessage | NewContactMessage {
    return {
      ...rawContactMessage,
      createdDate: dayjs(rawContactMessage.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertContactMessageToContactMessageRawValue(
    contactMessage: IContactMessage | (Partial<NewContactMessage> & ContactMessageFormDefaults),
  ): ContactMessageFormRawValue | PartialWithRequiredKeyOf<NewContactMessageFormRawValue> {
    return {
      ...contactMessage,
      createdDate: contactMessage.createdDate ? contactMessage.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
