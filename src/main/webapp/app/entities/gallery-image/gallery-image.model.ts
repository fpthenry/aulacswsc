import { IBranch } from 'app/entities/branch/branch.model';

export interface IGalleryImage {
  id: number;
  imageUrl?: string | null;
  captionVi?: string | null;
  captionEn?: string | null;
  altVi?: string | null;
  altEn?: string | null;
  width?: number | null;
  height?: number | null;
  displayOrder?: number | null;
  active?: boolean | null;
  branch?: Pick<IBranch, 'id' | 'nameVi'> | null;
}

export type NewGalleryImage = Omit<IGalleryImage, 'id'> & { id: null };
