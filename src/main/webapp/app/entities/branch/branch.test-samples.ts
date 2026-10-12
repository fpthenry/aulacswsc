import { IBranch, NewBranch } from './branch.model';

export const sampleWithRequiredData: IBranch = {
  id: 3155,
  nameVi: 'hydrolyse usually',
  nameEn: 'from',
  slug: 'yippee judicious',
  addressVi: 'per greatly',
  phone: '1-374-334-0942',
  openTime: '22:23',
  closeTime: '23:00',
  lastBookingTime: '23:20',
  capacity: 5123,
  active: true,
};

export const sampleWithPartialData: IBranch = {
  id: 20171,
  nameVi: 'pillbox eek',
  nameEn: 'chase trivial',
  slug: 'about woot as',
  addressVi: 'throughout',
  addressEn: 'gosh',
  phone: '564.241.5336 x383',
  whatsapp: 'between supposing sh',
  openTime: '20:12',
  closeTime: '18:16',
  lastBookingTime: '13:58',
  capacity: 16865,
  mapEmbedUrl: 'dimly irritably',
  mapDirectionUrl: 'kit defiantly',
  imageUrl: 'energetically',
  active: false,
};

export const sampleWithFullData: IBranch = {
  id: 25059,
  nameVi: 'whoever first',
  nameEn: 'at furthermore',
  slug: 'what federate yum',
  addressVi: 'pasta uh-huh ectoderm',
  addressEn: 'arrange unto',
  phone: '1-314-563-8547 x1521',
  whatsapp: 'self-assured wheneve',
  zalo: 'forenenst',
  email: 'Freida_Lemke@hotmail.com',
  openTime: '00:13',
  closeTime: '14:47',
  lastBookingTime: '22:21',
  capacity: 8644,
  mapEmbedUrl: 'gracefully how wherever',
  mapDirectionUrl: 'definite airbus',
  imageUrl: 'usefully orchestrate playfully',
  displayOrder: 30386,
  active: true,
};

export const sampleWithNewData: NewBranch = {
  nameVi: 'far',
  nameEn: 'angrily',
  slug: 'zowie',
  addressVi: 'apud',
  phone: '377-214-0101',
  openTime: '23:59',
  closeTime: '00:13',
  lastBookingTime: '03:05',
  capacity: 13106,
  active: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
