import dayjs from 'dayjs/esm';
import { BlogCategory } from 'app/entities/enumerations/blog-category.model';

export interface IBlogPost {
  id: number;
  titleVi?: string | null;
  titleEn?: string | null;
  slug?: string | null;
  excerptVi?: string | null;
  excerptEn?: string | null;
  contentVi?: string | null;
  contentEn?: string | null;
  coverImageUrl?: string | null;
  category?: keyof typeof BlogCategory | null;
  published?: boolean | null;
  publishedAt?: dayjs.Dayjs | null;
}

export type NewBlogPost = Omit<IBlogPost, 'id'> & { id: null };
