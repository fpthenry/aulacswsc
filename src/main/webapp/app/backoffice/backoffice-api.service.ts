import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { BookingConfirmation, BookingRequest } from 'app/site/core/site.models';

export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW';
export type PaymentStatus = 'UNPAID' | 'PAID' | 'REFUNDED';
export type PaymentMethod = 'CASH' | 'BANK_TRANSFER' | 'CARD' | 'E_WALLET';
export type BookingSource = 'WEBSITE' | 'QUICK' | 'ADMIN' | 'PHONE' | 'WALK_IN';

export interface Badges {
  pendingBookings: number;
  unhandledMessages: number;
  pendingReviews: number;
}

export interface BookingRow {
  id: number;
  code: string;
  startTime: string;
  endTime: string | null;
  numberOfPeople: number;
  status: BookingStatus;
  source: BookingSource | null;
  paymentStatus: PaymentStatus | null;
  paymentMethod: PaymentMethod | null;
  totalAmount: number | null;
  note: string | null;
  customerId: number | null;
  customerName: string | null;
  customerPhone: string | null;
  branchId: number | null;
  branchName: string | null;
  services: string[];
  staff: string[];
}

export interface Dashboard extends Badges {
  todayBookings: number;
  todayGuests: number;
  todayRevenue: number;
  monthRevenue: number;
  monthCompleted: number;
  todaySchedule: BookingRow[];
  next7Days: { date: string; bookings: number; guests: number }[];
  topServices: { id: number; name: string; count: number }[];
}

/** API trang quản trị (/api/backoffice/**, quyền ADMIN hoặc STAFF). */
@Injectable({ providedIn: 'root' })
export class BackofficeApiService {
  /** huy hiệu trên menu — các trang gọi refreshBadges() sau khi đổi dữ liệu */
  readonly badges = signal<Badges>({ pendingBookings: 0, unhandledMessages: 0, pendingReviews: 0 });

  private readonly http = inject(HttpClient);
  private readonly config = inject(ApplicationConfigService);

  dashboard(): Observable<Dashboard> {
    return this.http
      .get<Dashboard>(this.url('dashboard'))
      .pipe(
        tap(d =>
          this.badges.set({ pendingBookings: d.pendingBookings, unhandledMessages: d.unhandledMessages, pendingReviews: d.pendingReviews }),
        ),
      );
  }

  refreshBadges(): void {
    this.http.get<Badges>(this.url('badges')).subscribe({ next: b => this.badges.set(b), error: () => undefined });
  }

  bookings(
    date: string,
    days: number,
    branchId?: number | null,
    status?: BookingStatus | null,
    q?: string | null,
  ): Observable<BookingRow[]> {
    let params = new HttpParams().set('date', date).set('days', days);
    if (branchId) {
      params = params.set('branchId', branchId);
    }
    if (status) {
      params = params.set('status', status);
    }
    if (q?.trim()) {
      params = params.set('q', q.trim());
    }
    return this.http.get<BookingRow[]>(this.url('bookings'), { params });
  }

  updateStatus(
    id: number,
    status: BookingStatus,
    paymentStatus?: PaymentStatus | null,
    paymentMethod?: PaymentMethod | null,
  ): Observable<BookingRow> {
    return this.http
      .patch<BookingRow>(this.url(`bookings/${id}/status`), {
        status,
        paymentStatus: paymentStatus ?? null,
        paymentMethod: paymentMethod ?? null,
      })
      .pipe(tap(() => this.refreshBadges()));
  }

  quickCreate(request: BookingRequest, source: BookingSource): Observable<BookingConfirmation> {
    return this.http
      .post<BookingConfirmation>(this.url('bookings'), request, { params: new HttpParams().set('source', source) })
      .pipe(tap(() => this.refreshBadges()));
  }

  private url(path: string): string {
    return this.config.getEndpointFor(`api/backoffice/${path}`);
  }
}
