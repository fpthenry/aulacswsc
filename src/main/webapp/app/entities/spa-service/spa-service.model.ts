import { IServiceCategory } from 'app/entities/service-category/service-category.model';
import { IStaff } from 'app/entities/staff/staff.model';

export interface ISpaService {
  id: number;
  name?: string | null;
  slug?: string | null;
  shortDescription?: string | null;
  description?: string | null;
  price?: number | null;
  salePrice?: number | null;
  durationMinutes?: number | null;
  imageUrl?: string | null;
  featured?: boolean | null;
  active?: boolean | null;
  category?: Pick<IServiceCategory, 'id' | 'name'> | null;
  staffs?: Pick<IStaff, 'id'>[] | null;
}

export type NewSpaService = Omit<ISpaService, 'id'> & { id: null };
