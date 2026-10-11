import { ISpaService, NewSpaService } from './spa-service.model';

export const sampleWithRequiredData: ISpaService = {
  id: 24670,
  name: 'typify boohoo',
  slug: 'mockingly',
  price: 26836.78,
  durationMinutes: 25244,
  active: false,
};

export const sampleWithPartialData: ISpaService = {
  id: 13408,
  name: 'gently tusk pfft',
  slug: 'heartache',
  shortDescription: 'huzzah necklace',
  description: '../fake-data/blob/hipster.txt',
  price: 13607.03,
  salePrice: 993.77,
  durationMinutes: 11726,
  featured: false,
  active: false,
};

export const sampleWithFullData: ISpaService = {
  id: 3416,
  name: 'woot if',
  slug: 'foolish enlist until',
  shortDescription: 'rationalize',
  description: '../fake-data/blob/hipster.txt',
  price: 7686.21,
  salePrice: 10868.16,
  durationMinutes: 7646,
  imageUrl: 'feminize raw about',
  featured: true,
  active: true,
};

export const sampleWithNewData: NewSpaService = {
  name: 'aha save',
  slug: 'yuck',
  price: 15632.48,
  durationMinutes: 30530,
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
