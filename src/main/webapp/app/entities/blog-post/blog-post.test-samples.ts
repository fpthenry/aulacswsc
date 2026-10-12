import dayjs from 'dayjs/esm';

import { IBlogPost, NewBlogPost } from './blog-post.model';

export const sampleWithRequiredData: IBlogPost = {
  id: 13142,
  titleVi: 'enormously yowza below',
  slug: 'satirize ouch',
  published: false,
};

export const sampleWithPartialData: IBlogPost = {
  id: 12429,
  titleVi: 'ugh',
  slug: 'airline',
  excerptVi: 'nor for',
  contentVi: '../fake-data/blob/hipster.txt',
  contentEn: '../fake-data/blob/hipster.txt',
  coverImageUrl: 'punctuation',
  category: 'TIPS',
  published: true,
  publishedAt: dayjs('2026-10-11T00:18'),
};

export const sampleWithFullData: IBlogPost = {
  id: 10904,
  titleVi: 'well repeat hopelessly',
  titleEn: 'fashion',
  slug: 'conservative',
  excerptVi: 'paltry',
  excerptEn: 'shyly versus',
  contentVi: '../fake-data/blob/hipster.txt',
  contentEn: '../fake-data/blob/hipster.txt',
  coverImageUrl: 'sanity shush excepting',
  category: 'REVIEW',
  published: true,
  publishedAt: dayjs('2026-10-10T15:08'),
};

export const sampleWithNewData: NewBlogPost = {
  titleVi: 'with oh',
  slug: 'uh-huh annex dereference',
  published: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
