import dayjs from 'dayjs/esm';

export interface IContactMessage {
  id: number;
  fullName?: string | null;
  phone?: string | null;
  email?: string | null;
  message?: string | null;
  handled?: boolean | null;
  createdDate?: dayjs.Dayjs | null;
}

export type NewContactMessage = Omit<IContactMessage, 'id'> & { id: null };
