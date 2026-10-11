import dayjs from 'dayjs/esm';

export interface IPromotion {
  id: number;
  code?: string | null;
  name?: string | null;
  description?: string | null;
  discountPercent?: number | null;
  discountAmount?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  active?: boolean | null;
}

export type NewPromotion = Omit<IPromotion, 'id'> & { id: null };
