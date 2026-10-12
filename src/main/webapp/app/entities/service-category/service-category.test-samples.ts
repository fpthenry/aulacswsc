import { IServiceCategory, NewServiceCategory } from './service-category.model';

export const sampleWithRequiredData: IServiceCategory = {
  id: 10681,
  nameVi: 'drat',
  nameEn: 'fond',
  slug: 'concentration split oh',
  active: false,
};

export const sampleWithPartialData: IServiceCategory = {
  id: 6137,
  nameVi: 'since',
  nameEn: 'consequently',
  slug: 'access outnumber',
  descriptionEn: 'questionable boo',
  active: true,
};

export const sampleWithFullData: IServiceCategory = {
  id: 1300,
  nameVi: 'wherever',
  nameEn: 'psst',
  slug: 'ack',
  descriptionVi: 'converse not',
  descriptionEn: 'bashfully busy or',
  imageUrl: 'blah',
  displayOrder: 3394,
  active: true,
};

export const sampleWithNewData: NewServiceCategory = {
  nameVi: 'whenever why',
  nameEn: 'fax weekly factorise',
  slug: 'bah',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
