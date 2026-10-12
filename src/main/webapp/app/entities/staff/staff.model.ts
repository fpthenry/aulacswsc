import { IUser } from 'app/entities/user/user.model';
import { IBranch } from 'app/entities/branch/branch.model';
import { ISpaService } from 'app/entities/spa-service/spa-service.model';

export interface IStaff {
  id: number;
  fullName?: string | null;
  phone?: string | null;
  position?: string | null;
  bio?: string | null;
  avatarUrl?: string | null;
  active?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  branch?: Pick<IBranch, 'id' | 'nameVi'> | null;
  services?: Pick<ISpaService, 'id' | 'nameVi'>[] | null;
}

export type NewStaff = Omit<IStaff, 'id'> & { id: null };
