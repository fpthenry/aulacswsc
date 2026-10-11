import dayjs from 'dayjs/esm';

import { IContactMessage, NewContactMessage } from './contact-message.model';

export const sampleWithRequiredData: IContactMessage = {
  id: 9760,
  fullName: 'criminal yum',
  message: 'perfection',
};

export const sampleWithPartialData: IContactMessage = {
  id: 32469,
  fullName: 'until deliberately crank',
  phone: '1-676-215-0541',
  email: 'Britney.Hermiston@yahoo.com',
  message: 'linseed worth',
};

export const sampleWithFullData: IContactMessage = {
  id: 5510,
  fullName: 'almost along judgementally',
  phone: '1-442-919-2325 x9246',
  email: 'Bernie13@gmail.com',
  message: 'vice',
  handled: false,
  createdDate: dayjs('2026-10-10T05:12'),
};

export const sampleWithNewData: NewContactMessage = {
  fullName: 'snuggle',
  message: 'community',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
