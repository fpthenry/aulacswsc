import dayjs from 'dayjs/esm';

import { IReview, NewReview } from './review.model';

export const sampleWithRequiredData: IReview = {
  id: 6640,
  authorName: 'quizzically',
  rating: 2,
};

export const sampleWithPartialData: IReview = {
  id: 27633,
  authorName: 'tut ecstatic',
  rating: 2,
  videoUrl: 'utilization',
  reviewDate: dayjs('2026-10-10'),
  approved: true,
  featured: false,
};

export const sampleWithFullData: IReview = {
  id: 30916,
  authorName: 'cornet commodity keenly',
  rating: 1,
  comment: 'cosset',
  videoUrl: 'upbeat acidly ugh',
  reviewDate: dayjs('2026-10-11'),
  approved: false,
  featured: false,
  createdDate: dayjs('2026-10-11T04:28'),
};

export const sampleWithNewData: NewReview = {
  authorName: 'private yuck',
  rating: 5,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
