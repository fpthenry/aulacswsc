import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface ICustomer {
  id: number;
  fullName?: string | null;
  phone?: string | null;
  email?: string | null;
  gender?: keyof typeof Gender | null;
  dateOfBirth?: dayjs.Dayjs | null;
  address?: string | null;
  note?: string | null;
  createdDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
