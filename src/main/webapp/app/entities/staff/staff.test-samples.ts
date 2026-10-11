import { IStaff, NewStaff } from './staff.model';

export const sampleWithRequiredData: IStaff = {
  id: 7442,
  fullName: 'silently',
  active: false,
};

export const sampleWithPartialData: IStaff = {
  id: 29006,
  fullName: 'commonly gosh hexagon',
  phone: '1-531-236-4234 x9458',
  position: 'bah reopen square',
  active: false,
};

export const sampleWithFullData: IStaff = {
  id: 9541,
  fullName: 'accidentally',
  phone: '348.827.0434 x5126',
  position: 'modulo',
  bio: '../fake-data/blob/hipster.txt',
  avatarUrl: 'season difficult service',
  active: false,
};

export const sampleWithNewData: NewStaff = {
  fullName: 'until',
  active: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
