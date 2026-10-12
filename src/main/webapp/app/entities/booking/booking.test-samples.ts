import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 4955,
  code: 'annually',
  startTime: dayjs('2026-10-10T20:49'),
  numberOfPeople: 10,
  status: 'CONFIRMED',
};

export const sampleWithPartialData: IBooking = {
  id: 11967,
  code: 'scorpion',
  startTime: dayjs('2026-10-10T05:06'),
  endTime: dayjs('2026-10-10T19:43'),
  numberOfPeople: 20,
  status: 'CANCELLED',
  totalAmount: 8832.12,
  discountAmount: 15149.24,
  paymentStatus: 'PAID',
  note: 'ah unique',
};

export const sampleWithFullData: IBooking = {
  id: 28482,
  code: 'pish despite',
  startTime: dayjs('2026-10-10T11:52'),
  endTime: dayjs('2026-10-10T21:20'),
  numberOfPeople: 10,
  status: 'NO_SHOW',
  source: 'WALK_IN',
  totalAmount: 23409.09,
  discountAmount: 1594.39,
  paymentStatus: 'PAID',
  paymentMethod: 'BANK_TRANSFER',
  note: 'purse trusty',
  createdDate: dayjs('2026-10-10T22:37'),
};

export const sampleWithNewData: NewBooking = {
  code: 'glossy',
  startTime: dayjs('2026-10-10T16:11'),
  numberOfPeople: 18,
  status: 'PENDING',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
