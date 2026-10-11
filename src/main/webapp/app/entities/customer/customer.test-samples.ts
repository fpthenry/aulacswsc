import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 3366,
  fullName: 'slake splurge',
  phone: '533.445.3719 x424',
};

export const sampleWithPartialData: ICustomer = {
  id: 31728,
  fullName: 'garage circa metallic',
  phone: '978-708-2397 x86912',
  note: '../fake-data/blob/hipster.txt',
  createdDate: dayjs('2026-10-10T08:33'),
};

export const sampleWithFullData: ICustomer = {
  id: 4149,
  fullName: 'where known',
  phone: '779-272-6432',
  email: 'Adeline.Trantow@gmail.com',
  gender: 'FEMALE',
  dateOfBirth: dayjs('2026-10-10'),
  address: 'next',
  note: '../fake-data/blob/hipster.txt',
  createdDate: dayjs('2026-10-10T13:07'),
};

export const sampleWithNewData: NewCustomer = {
  fullName: 'thyme inasmuch',
  phone: '1-826-447-1957',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
