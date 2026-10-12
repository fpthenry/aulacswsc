/**
 * Kiểu dữ liệu JSON trả về từ /api/public/** (khớp các DTO Java trong service/dto và service/dto/site).
 * Trường song ngữ luôn đi theo cặp xxxVi / xxxEn — dùng hàm pick() để lấy theo ngôn ngữ.
 */

export interface SiteSetting {
  siteName?: string | null;
  logoUrl?: string | null;
  sloganVi?: string | null;
  sloganEn?: string | null;
  heroSubtitleVi?: string | null;
  heroSubtitleEn?: string | null;
  heroImageUrl?: string | null;
  heroVideoUrl?: string | null;
  heroVideoEnabled?: boolean | null;
  /** mỗi dòng một URL ảnh slideshow đầu trang */
  heroSlides?: string | null;
  backgroundMusicUrl?: string | null;
  aboutTitleVi?: string | null;
  aboutTitleEn?: string | null;
  aboutContentVi?: string | null;
  aboutContentEn?: string | null;
  highlight1TitleVi?: string | null;
  highlight1TitleEn?: string | null;
  highlight1TextVi?: string | null;
  highlight1TextEn?: string | null;
  highlight2TitleVi?: string | null;
  highlight2TitleEn?: string | null;
  highlight2TextVi?: string | null;
  highlight2TextEn?: string | null;
  highlight3TitleVi?: string | null;
  highlight3TitleEn?: string | null;
  highlight3TextVi?: string | null;
  highlight3TextEn?: string | null;
  hotline?: string | null;
  email?: string | null;
  whatsappNumber?: string | null;
  zaloNumber?: string | null;
  messengerUrl?: string | null;
  facebookUrl?: string | null;
  instagramUrl?: string | null;
  tiktokUrl?: string | null;
  youtubeUrl?: string | null;
  openingHoursVi?: string | null;
  openingHoursEn?: string | null;
  metaDescriptionVi?: string | null;
  metaDescriptionEn?: string | null;
  policyContentVi?: string | null;
  policyContentEn?: string | null;
}

export interface Branch {
  id: number;
  nameVi: string;
  nameEn: string;
  slug: string;
  addressVi: string;
  addressEn?: string | null;
  phone: string;
  whatsapp?: string | null;
  zalo?: string | null;
  email?: string | null;
  openTime: string;
  closeTime: string;
  lastBookingTime: string;
  capacity: number;
  mapEmbedUrl?: string | null;
  mapDirectionUrl?: string | null;
  imageUrl?: string | null;
}

export interface ServiceCategory {
  id: number;
  nameVi: string;
  nameEn: string;
  slug: string;
  descriptionVi?: string | null;
  descriptionEn?: string | null;
  imageUrl?: string | null;
}

export interface SpaService {
  id: number;
  nameVi: string;
  nameEn: string;
  slug: string;
  shortDescriptionVi?: string | null;
  shortDescriptionEn?: string | null;
  descriptionVi?: string | null;
  descriptionEn?: string | null;
  benefitsVi?: string | null;
  benefitsEn?: string | null;
  notesVi?: string | null;
  notesEn?: string | null;
  price: number;
  salePrice?: number | null;
  priceMax?: number | null;
  durationMinutes: number;
  imageUrl?: string | null;
  featured?: boolean | null;
  category?: { id: number } | null;
}

export interface SiteBootstrap {
  setting: SiteSetting;
  branches: Branch[];
  categories: ServiceCategory[];
  services: SpaService[];
}

export interface Review {
  id: number;
  authorName: string;
  rating: number;
  comment?: string | null;
  videoUrl?: string | null;
  reviewDate?: string | null;
  featured?: boolean | null;
  service?: { id: number; nameVi?: string } | null;
}

export interface GalleryImage {
  id: number;
  imageUrl: string;
  captionVi?: string | null;
  captionEn?: string | null;
  altVi: string;
  altEn?: string | null;
  width?: number | null;
  height?: number | null;
}

export type BlogCategory = 'NEWS' | 'EVENT' | 'REVIEW' | 'TIPS';

export interface BlogPost {
  id: number;
  titleVi: string;
  titleEn?: string | null;
  slug: string;
  excerptVi?: string | null;
  excerptEn?: string | null;
  contentVi?: string | null;
  contentEn?: string | null;
  coverImageUrl?: string | null;
  category?: BlogCategory | null;
  publishedAt?: string | null;
}

export type SlotStatus = 'AVAILABLE' | 'FULL' | 'PAST';

export interface TimeSlot {
  time: string;
  status: SlotStatus;
}

export interface BookingRequest {
  branchId: number | null;
  serviceIds: number[];
  date: string;
  time: string;
  numberOfPeople: number;
  fullName: string;
  phone: string;
  email: string | null;
  note: string | null;
  quick: boolean;
  /** honeypot: người thật để trống */
  website: string;
}

export interface BookingConfirmation {
  code: string;
  status: string;
  startTime: string;
  endTime: string;
  numberOfPeople: number;
  totalDurationMinutes: number;
  totalAmount: number;
  customerName: string;
  phone: string;
  email?: string | null;
  note?: string | null;
  branch: {
    id: number;
    nameVi: string;
    nameEn: string;
    addressVi: string;
    addressEn?: string | null;
    phone: string;
    mapDirectionUrl?: string | null;
  };
  services: {
    id: number;
    nameVi: string;
    nameEn: string;
    unitPrice: number;
    quantity: number;
    durationMinutes: number;
  }[];
}

export interface Paged<T> {
  items: T[];
  total: number;
}
