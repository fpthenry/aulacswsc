import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBooking, NewBooking } from '../booking.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBooking for edit and NewBookingFormGroupInput for create.
 */
type BookingFormGroupInput = IBooking | PartialWithRequiredKeyOf<NewBooking>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBooking | NewBooking> = Omit<T, 'startTime' | 'endTime' | 'createdDate'> & {
  startTime?: string | null;
  endTime?: string | null;
  createdDate?: string | null;
};

type BookingFormRawValue = FormValueOf<IBooking>;

type NewBookingFormRawValue = FormValueOf<NewBooking>;

type BookingFormDefaults = Pick<NewBooking, 'id' | 'startTime' | 'endTime' | 'createdDate'>;

type BookingFormGroupContent = {
  id: FormControl<BookingFormRawValue['id'] | NewBooking['id']>;
  code: FormControl<BookingFormRawValue['code']>;
  startTime: FormControl<BookingFormRawValue['startTime']>;
  endTime: FormControl<BookingFormRawValue['endTime']>;
  numberOfPeople: FormControl<BookingFormRawValue['numberOfPeople']>;
  status: FormControl<BookingFormRawValue['status']>;
  source: FormControl<BookingFormRawValue['source']>;
  totalAmount: FormControl<BookingFormRawValue['totalAmount']>;
  discountAmount: FormControl<BookingFormRawValue['discountAmount']>;
  paymentStatus: FormControl<BookingFormRawValue['paymentStatus']>;
  paymentMethod: FormControl<BookingFormRawValue['paymentMethod']>;
  note: FormControl<BookingFormRawValue['note']>;
  createdDate: FormControl<BookingFormRawValue['createdDate']>;
  branch: FormControl<BookingFormRawValue['branch']>;
  promotion: FormControl<BookingFormRawValue['promotion']>;
  customer: FormControl<BookingFormRawValue['customer']>;
};

export type BookingFormGroup = FormGroup<BookingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingFormService {
  createBookingFormGroup(booking: BookingFormGroupInput = { id: null }): BookingFormGroup {
    const bookingRawValue = this.convertBookingToBookingRawValue({
      ...this.getFormDefaults(),
      ...booking,
    });
    return new FormGroup<BookingFormGroupContent>({
      id: new FormControl(
        { value: bookingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(bookingRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      startTime: new FormControl(bookingRawValue.startTime, {
        validators: [Validators.required],
      }),
      endTime: new FormControl(bookingRawValue.endTime),
      numberOfPeople: new FormControl(bookingRawValue.numberOfPeople, {
        validators: [Validators.required, Validators.min(1), Validators.max(20)],
      }),
      status: new FormControl(bookingRawValue.status, {
        validators: [Validators.required],
      }),
      source: new FormControl(bookingRawValue.source),
      totalAmount: new FormControl(bookingRawValue.totalAmount, {
        validators: [Validators.min(0)],
      }),
      discountAmount: new FormControl(bookingRawValue.discountAmount, {
        validators: [Validators.min(0)],
      }),
      paymentStatus: new FormControl(bookingRawValue.paymentStatus),
      paymentMethod: new FormControl(bookingRawValue.paymentMethod),
      note: new FormControl(bookingRawValue.note, {
        validators: [Validators.maxLength(1000)],
      }),
      createdDate: new FormControl(bookingRawValue.createdDate),
      branch: new FormControl(bookingRawValue.branch, {
        validators: [Validators.required],
      }),
      promotion: new FormControl(bookingRawValue.promotion),
      customer: new FormControl(bookingRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getBooking(form: BookingFormGroup): IBooking | NewBooking {
    return this.convertBookingRawValueToBooking(form.getRawValue() as BookingFormRawValue | NewBookingFormRawValue);
  }

  resetForm(form: BookingFormGroup, booking: BookingFormGroupInput): void {
    const bookingRawValue = this.convertBookingToBookingRawValue({ ...this.getFormDefaults(), ...booking });
    form.reset(
      {
        ...bookingRawValue,
        id: { value: bookingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
      createdDate: currentTime,
    };
  }

  private convertBookingRawValueToBooking(rawBooking: BookingFormRawValue | NewBookingFormRawValue): IBooking | NewBooking {
    return {
      ...rawBooking,
      startTime: dayjs(rawBooking.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawBooking.endTime, DATE_TIME_FORMAT),
      createdDate: dayjs(rawBooking.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertBookingToBookingRawValue(
    booking: IBooking | (Partial<NewBooking> & BookingFormDefaults),
  ): BookingFormRawValue | PartialWithRequiredKeyOf<NewBookingFormRawValue> {
    return {
      ...booking,
      startTime: booking.startTime ? booking.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: booking.endTime ? booking.endTime.format(DATE_TIME_FORMAT) : undefined,
      createdDate: booking.createdDate ? booking.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
