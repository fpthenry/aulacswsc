import dayjs from 'dayjs/esm';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

export interface IBooking {
  id: number;
  code?: string | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  status?: keyof typeof BookingStatus | null;
  totalAmount?: number | null;
  discountAmount?: number | null;
  paymentStatus?: keyof typeof PaymentStatus | null;
  paymentMethod?: keyof typeof PaymentMethod | null;
  note?: string | null;
  createdDate?: dayjs.Dayjs | null;
  promotion?: Pick<IPromotion, 'id' | 'code'> | null;
  customer?: Pick<ICustomer, 'id' | 'fullName'> | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
