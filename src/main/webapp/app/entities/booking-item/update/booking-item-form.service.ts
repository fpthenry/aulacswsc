import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBookingItem, NewBookingItem } from '../booking-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookingItem for edit and NewBookingItemFormGroupInput for create.
 */
type BookingItemFormGroupInput = IBookingItem | PartialWithRequiredKeyOf<NewBookingItem>;

type BookingItemFormDefaults = Pick<NewBookingItem, 'id'>;

type BookingItemFormGroupContent = {
  id: FormControl<IBookingItem['id'] | NewBookingItem['id']>;
  price: FormControl<IBookingItem['price']>;
  quantity: FormControl<IBookingItem['quantity']>;
  durationMinutes: FormControl<IBookingItem['durationMinutes']>;
  service: FormControl<IBookingItem['service']>;
  staff: FormControl<IBookingItem['staff']>;
  booking: FormControl<IBookingItem['booking']>;
};

export type BookingItemFormGroup = FormGroup<BookingItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingItemFormService {
  createBookingItemFormGroup(bookingItem: BookingItemFormGroupInput = { id: null }): BookingItemFormGroup {
    const bookingItemRawValue = {
      ...this.getFormDefaults(),
      ...bookingItem,
    };
    return new FormGroup<BookingItemFormGroupContent>({
      id: new FormControl(
        { value: bookingItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      price: new FormControl(bookingItemRawValue.price, {
        validators: [Validators.required, Validators.min(0)],
      }),
      quantity: new FormControl(bookingItemRawValue.quantity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      durationMinutes: new FormControl(bookingItemRawValue.durationMinutes),
      service: new FormControl(bookingItemRawValue.service, {
        validators: [Validators.required],
      }),
      staff: new FormControl(bookingItemRawValue.staff),
      booking: new FormControl(bookingItemRawValue.booking, {
        validators: [Validators.required],
      }),
    });
  }

  getBookingItem(form: BookingItemFormGroup): IBookingItem | NewBookingItem {
    return form.getRawValue() as IBookingItem | NewBookingItem;
  }

  resetForm(form: BookingItemFormGroup, bookingItem: BookingItemFormGroupInput): void {
    const bookingItemRawValue = { ...this.getFormDefaults(), ...bookingItem };
    form.reset(
      {
        ...bookingItemRawValue,
        id: { value: bookingItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookingItemFormDefaults {
    return {
      id: null,
    };
  }
}
