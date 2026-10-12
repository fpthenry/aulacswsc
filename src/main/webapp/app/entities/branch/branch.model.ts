export interface IBranch {
  id: number;
  nameVi?: string | null;
  nameEn?: string | null;
  slug?: string | null;
  addressVi?: string | null;
  addressEn?: string | null;
  phone?: string | null;
  whatsapp?: string | null;
  zalo?: string | null;
  email?: string | null;
  openTime?: string | null;
  closeTime?: string | null;
  lastBookingTime?: string | null;
  capacity?: number | null;
  mapEmbedUrl?: string | null;
  mapDirectionUrl?: string | null;
  imageUrl?: string | null;
  displayOrder?: number | null;
  active?: boolean | null;
}

export type NewBranch = Omit<IBranch, 'id'> & { id: null };
