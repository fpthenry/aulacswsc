import { ISpaService, NewSpaService } from './spa-service.model';

export const sampleWithRequiredData: ISpaService = {
  id: 24670,
  nameVi: 'typify boohoo',
  nameEn: 'mockingly',
  slug: 'but gently mousse',
  price: 13039.69,
  durationMinutes: 2388,
  active: true,
};

export const sampleWithPartialData: ISpaService = {
  id: 9550,
  nameVi: 'barring avalanche',
  nameEn: 'queasily',
  slug: 'lend except separately',
  shortDescriptionVi: 'yippee horst',
  shortDescriptionEn: 'indeed',
  descriptionVi: '../fake-data/blob/hipster.txt',
  benefitsVi: '../fake-data/blob/hipster.txt',
  benefitsEn: '../fake-data/blob/hipster.txt',
  price: 14284.5,
  salePrice: 7220.31,
  durationMinutes: 7144,
  featured: true,
  displayOrder: 9158,
  active: true,
};

export const sampleWithFullData: ISpaService = {
  id: 3416,
  nameVi: 'woot if',
  nameEn: 'foolish enlist until',
  slug: 'rationalize',
  shortDescriptionVi: 'yuck',
  shortDescriptionEn: 'useless wetly aw',
  descriptionVi: '../fake-data/blob/hipster.txt',
  descriptionEn: '../fake-data/blob/hipster.txt',
  benefitsVi: '../fake-data/blob/hipster.txt',
  benefitsEn: '../fake-data/blob/hipster.txt',
  notesVi: '../fake-data/blob/hipster.txt',
  notesEn: '../fake-data/blob/hipster.txt',
  price: 10007.09,
  salePrice: 32602.47,
  priceMax: 23607.62,
  durationMinutes: 425,
  imageUrl: 'instead hm',
  featured: true,
  displayOrder: 15226,
  active: false,
};

export const sampleWithNewData: NewSpaService = {
  nameVi: 'aha save',
  nameEn: 'yuck',
  slug: 'duh regarding',
  price: 11587.17,
  durationMinutes: 28035,
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
