import { Injectable, computed, effect, inject, signal } from '@angular/core';

import { SiteStateService } from './site-state.service';
import { SpaService } from './site.models';

/** Bản nháp đặt lịch — cũng chính là "giỏ dịch vụ" khi khách bấm "Chọn" ở thẻ dịch vụ. */
export interface BookingDraft {
  branchId: number | null;
  serviceIds: number[];
  /** yyyy-MM-dd */
  date: string | null;
  /** HH:mm */
  time: string | null;
  people: number;
  fullName: string;
  phone: string;
  email: string;
  note: string;
  /** bước hiện tại của luồng đặt lịch (1..7) */
  step: number;
}

const STORAGE_KEY = 'site.bookingDraft';
const EMPTY: BookingDraft = {
  branchId: null,
  serviceIds: [],
  date: null,
  time: null,
  people: 1,
  fullName: '',
  phone: '',
  email: '',
  note: '',
  step: 1,
};

/**
 * Lưu nháp vào localStorage để khách tải lại trang không mất dữ liệu.
 * Nháp bị xóa ngay khi đặt lịch thành công (không giữ thông tin cá nhân lâu hơn cần thiết).
 */
@Injectable({ providedIn: 'root' })
export class BookingDraftService {
  private readonly state = inject(SiteStateService);

  readonly draft = signal<BookingDraft>(this.restore());

  readonly selectedServices = computed<SpaService[]>(() =>
    this.draft()
      .serviceIds.map(id => this.state.serviceById(id))
      .filter((s): s is SpaService => !!s),
  );
  readonly count = computed(() => this.draft().serviceIds.length);
  readonly totalDuration = computed(() => this.selectedServices().reduce((sum, s) => sum + s.durationMinutes, 0));
  /** Giá tạm tính: giá khuyến mãi (nếu có) × số người. Giá chính thức do server tính. */
  readonly totalPrice = computed(() => this.selectedServices().reduce((sum, s) => sum + unitPrice(s), 0) * this.draft().people);

  constructor() {
    effect(() => {
      const value = this.draft();
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
      } catch {
        // localStorage bị chặn → bỏ qua
      }
    });
  }

  update(patch: Partial<BookingDraft>): void {
    this.draft.update(d => ({ ...d, ...patch }));
  }

  has(serviceId: number): boolean {
    return this.draft().serviceIds.includes(serviceId);
  }

  toggle(serviceId: number): void {
    this.draft.update(d => ({
      ...d,
      serviceIds: d.serviceIds.includes(serviceId) ? d.serviceIds.filter(id => id !== serviceId) : [...d.serviceIds, serviceId],
      // đổi dịch vụ → thời lượng đổi → giờ đã chọn có thể không còn hợp lệ
      time: null,
    }));
  }

  clear(): void {
    this.draft.set({ ...EMPTY });
  }

  private restore(): BookingDraft {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw) as Partial<BookingDraft>;
        return { ...EMPTY, ...parsed, serviceIds: Array.isArray(parsed.serviceIds) ? parsed.serviceIds.filter(Number.isFinite) : [] };
      }
    } catch {
      // dữ liệu hỏng → bắt đầu lại
    }
    return { ...EMPTY };
  }
}

export function unitPrice(service: SpaService): number {
  return service.salePrice ?? service.price;
}
