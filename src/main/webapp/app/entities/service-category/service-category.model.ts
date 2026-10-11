export interface IServiceCategory {
  id: number;
  name?: string | null;
  slug?: string | null;
  description?: string | null;
  imageUrl?: string | null;
  displayOrder?: number | null;
  active?: boolean | null;
}

export type NewServiceCategory = Omit<IServiceCategory, 'id'> & { id: null };
