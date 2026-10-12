import { IServiceCategory } from 'app/entities/service-category/service-category.model';
import { IStaff } from 'app/entities/staff/staff.model';

export interface ISpaService {
  id: number;
  nameVi?: string | null;
  nameEn?: string | null;
  slug?: string | null;
  shortDescriptionVi?: string | null;
  shortDescriptionEn?: string | null;
  descriptionVi?: string | null;
  descriptionEn?: string | null;
  benefitsVi?: string | null;
  benefitsEn?: string | null;
  notesVi?: string | null;
  notesEn?: string | null;
  price?: number | null;
  salePrice?: number | null;
  priceMax?: number | null;
  durationMinutes?: number | null;
  imageUrl?: string | null;
  featured?: boolean | null;
  displayOrder?: number | null;
  active?: boolean | null;
  category?: Pick<IServiceCategory, 'id' | 'nameVi'> | null;
  staffs?: Pick<IStaff, 'id'>[] | null;
}

export type NewSpaService = Omit<ISpaService, 'id'> & { id: null };
