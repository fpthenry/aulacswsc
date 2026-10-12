export interface IServiceCategory {
  id: number;
  nameVi?: string | null;
  nameEn?: string | null;
  slug?: string | null;
  descriptionVi?: string | null;
  descriptionEn?: string | null;
  imageUrl?: string | null;
  displayOrder?: number | null;
  active?: boolean | null;
}

export type NewServiceCategory = Omit<IServiceCategory, 'id'> & { id: null };
