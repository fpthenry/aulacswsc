import dayjs from 'dayjs/esm';

import { IPromotion, NewPromotion } from './promotion.model';

export const sampleWithRequiredData: IPromotion = {
  id: 12646,
  code: 'clear aha',
  name: 'inasmuch er',
  active: false,
};

export const sampleWithPartialData: IPromotion = {
  id: 26248,
  code: 'round canter',
  name: 'pish clean',
  description: 'mmm mysterious even',
  discountPercent: 34,
  discountAmount: 21340.38,
  active: false,
};

export const sampleWithFullData: IPromotion = {
  id: 16704,
  code: 'pish',
  name: 'daintily hmph fictionalize',
  description: 'than greedily',
  discountPercent: 67,
  discountAmount: 21085.09,
  startDate: dayjs('2026-10-10T22:16'),
  endDate: dayjs('2026-10-10T10:32'),
  active: false,
};

export const sampleWithNewData: NewPromotion = {
  code: 'quizzically pomelo',
  name: 'openly yowza knavishly',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
