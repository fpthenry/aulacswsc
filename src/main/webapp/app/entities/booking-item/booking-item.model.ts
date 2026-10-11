import { ISpaService } from 'app/entities/spa-service/spa-service.model';
import { IStaff } from 'app/entities/staff/staff.model';
import { IBooking } from 'app/entities/booking/booking.model';

export interface IBookingItem {
  id: number;
  price?: number | null;
  quantity?: number | null;
  durationMinutes?: number | null;
  service?: Pick<ISpaService, 'id' | 'name'> | null;
  staff?: Pick<IStaff, 'id' | 'fullName'> | null;
  booking?: Pick<IBooking, 'id' | 'code'> | null;
}

export type NewBookingItem = Omit<IBookingItem, 'id'> & { id: null };
