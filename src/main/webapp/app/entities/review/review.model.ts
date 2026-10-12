import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { ISpaService } from 'app/entities/spa-service/spa-service.model';

export interface IReview {
  id: number;
  authorName?: string | null;
  rating?: number | null;
  comment?: string | null;
  videoUrl?: string | null;
  reviewDate?: dayjs.Dayjs | null;
  approved?: boolean | null;
  featured?: boolean | null;
  createdDate?: dayjs.Dayjs | null;
  customer?: Pick<ICustomer, 'id' | 'fullName'> | null;
  service?: Pick<ISpaService, 'id' | 'nameVi'> | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
