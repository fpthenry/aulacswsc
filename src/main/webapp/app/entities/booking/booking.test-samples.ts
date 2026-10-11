import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 4955,
  code: 'annually',
  startTime: dayjs('2026-10-10T20:49'),
  status: 'IN_PROGRESS',
};

export const sampleWithPartialData: IBooking = {
  id: 30120,
  code: 'governance lovingly',
  startTime: dayjs('2026-10-10T16:19'),
  endTime: dayjs('2026-10-10T16:11'),
  status: 'PENDING',
  discountAmount: 12522.65,
  paymentStatus: 'PAID',
  paymentMethod: 'BANK_TRANSFER',
  createdDate: dayjs('2026-10-10T23:56'),
};

export const sampleWithFullData: IBooking = {
  id: 28482,
  code: 'pish despite',
  startTime: dayjs('2026-10-10T11:52'),
  endTime: dayjs('2026-10-10T21:20'),
  status: 'IN_PROGRESS',
  totalAmount: 31888.23,
  discountAmount: 32716.66,
  paymentStatus: 'REFUNDED',
  paymentMethod: 'CASH',
  note: 'amid shakily',
  createdDate: dayjs('2026-10-11T03:09'),
};

export const sampleWithNewData: NewBooking = {
  code: 'glossy',
  startTime: dayjs('2026-10-10T16:11'),
  status: 'NO_SHOW',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
