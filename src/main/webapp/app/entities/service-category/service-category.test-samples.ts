import { IServiceCategory, NewServiceCategory } from './service-category.model';

export const sampleWithRequiredData: IServiceCategory = {
  id: 10681,
  name: 'drat',
  slug: 'fond',
  active: false,
};

export const sampleWithPartialData: IServiceCategory = {
  id: 26927,
  name: 'whoever',
  slug: 'imaginary department',
  imageUrl: 'inasmuch whereas',
  active: false,
};

export const sampleWithFullData: IServiceCategory = {
  id: 1300,
  name: 'wherever',
  slug: 'psst',
  description: '../fake-data/blob/hipster.txt',
  imageUrl: 'ack',
  displayOrder: 16495,
  active: false,
};

export const sampleWithNewData: NewServiceCategory = {
  name: 'whenever why',
  slug: 'fax weekly factorise',
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
