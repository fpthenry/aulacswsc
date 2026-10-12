import { IGalleryImage, NewGalleryImage } from './gallery-image.model';

export const sampleWithRequiredData: IGalleryImage = {
  id: 29073,
  imageUrl: 'that bravely unlike',
  altVi: 'tame sniveling intervention',
  active: true,
};

export const sampleWithPartialData: IGalleryImage = {
  id: 16893,
  imageUrl: 'on now',
  captionVi: 'provided widow after',
  altVi: 'hmph corporation resort',
  altEn: 'separate boohoo disappointment',
  displayOrder: 6054,
  active: false,
};

export const sampleWithFullData: IGalleryImage = {
  id: 32491,
  imageUrl: 'enrage potentially below',
  captionVi: 'than',
  captionEn: 'athwart likewise gee',
  altVi: 'noxious likewise',
  altEn: 'whereas measly',
  width: 24433,
  height: 5411,
  displayOrder: 15229,
  active: true,
};

export const sampleWithNewData: NewGalleryImage = {
  imageUrl: 'adjudge beneath',
  altVi: 'psst',
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
