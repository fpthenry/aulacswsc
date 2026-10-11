import { IBookingItem, NewBookingItem } from './booking-item.model';

export const sampleWithRequiredData: IBookingItem = {
  id: 25387,
  price: 21556.07,
  quantity: 18666,
};

export const sampleWithPartialData: IBookingItem = {
  id: 13848,
  price: 8982.94,
  quantity: 7414,
  durationMinutes: 16345,
};

export const sampleWithFullData: IBookingItem = {
  id: 1295,
  price: 19139.91,
  quantity: 5323,
  durationMinutes: 25861,
};

export const sampleWithNewData: NewBookingItem = {
  price: 24914.61,
  quantity: 24931,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
