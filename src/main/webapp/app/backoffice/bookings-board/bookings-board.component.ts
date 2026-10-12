import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { SiteApiService } from 'app/site/core/site-api.service';
import { Branch, SpaService } from 'app/site/core/site.models';
import { addDays, formatPrice, formatTime, normalizeVnPhone, todayVn, zaloLink } from 'app/site/core/site-utils';
import { SiteIconComponent } from 'app/site/shared/site-icon.component';
import { BackofficeApiService, BookingRow, BookingSource, BookingStatus, PaymentMethod } from '../backoffice-api.service';

interface Action {
  label: string;
  status: BookingStatus;
  tone: 'primary' | 'success' | 'danger' | 'default';
  confirm?: string;
}

/** Nút thao tác theo trạng thái hiện tại (luồng: Chờ xác nhận → Đã xác nhận → Đang phục vụ → Hoàn thành). */
const ACTIONS: Record<BookingStatus, Action[]> = {
  PENDING: [
    { label: 'Xác nhận', status: 'CONFIRMED', tone: 'primary' },
    { label: 'Hủy', status: 'CANCELLED', tone: 'danger', confirm: 'Hủy lịch hẹn này?' },
  ],
  CONFIRMED: [
    { label: 'Khách đã đến', status: 'IN_PROGRESS', tone: 'primary' },
    { label: 'Không đến', status: 'NO_SHOW', tone: 'default', confirm: 'Đánh dấu khách không đến?' },
    { label: 'Hủy', status: 'CANCELLED', tone: 'danger', confirm: 'Hủy lịch hẹn này?' },
  ],
  IN_PROGRESS: [],
  COMPLETED: [],
  CANCELLED: [{ label: 'Khôi phục', status: 'CONFIRMED', tone: 'default' }],
  NO_SHOW: [{ label: 'Khôi phục', status: 'CONFIRMED', tone: 'default' }],
};

interface QuickForm {
  branchId: number | null;
  date: string;
  time: string;
  serviceIds: number[];
  people: number;
  fullName: string;
  phone: string;
  note: string;
  source: BookingSource;
}

const STATUSES: BookingStatus[] = ['PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW'];

/**
 * Bảng lịch hẹn cho lễ tân: xem theo ngày / 7 ngày, lọc, tìm kiếm, đổi trạng thái một chạm, thu tiền, tạo nhanh lịch.
 */
@Component({
  selector: 'jhi-bookings-board',
  templateUrl: './bookings-board.component.html',
  styleUrl: './bookings-board.component.scss',
  imports: [RouterLink, TranslateModule, SiteIconComponent],
})
export default class BookingsBoardComponent implements OnInit {
  readonly api = inject(BackofficeApiService);

  readonly statuses = STATUSES;
  readonly actionsFor = ACTIONS;
  readonly payMethods: PaymentMethod[] = ['CASH', 'BANK_TRANSFER', 'CARD', 'E_WALLET'];

  // ---- bộ lọc (đồng bộ với URL để chia sẻ / quay lại) ----
  readonly date = signal(todayVn());
  readonly days = signal(1);
  readonly branchId = signal<number | null>(null);
  readonly status = signal<BookingStatus | null>(null);
  readonly q = signal('');

  readonly rows = signal<BookingRow[]>([]);
  readonly loading = signal(false);
  readonly busyId = signal<number | null>(null);
  /** lịch đang mở hộp "Hoàn thành & thu tiền" */
  readonly payingId = signal<number | null>(null);
  readonly toast = signal<string | null>(null);

  readonly branches = signal<Branch[]>([]);
  readonly services = signal<SpaService[]>([]);

  readonly isToday = computed(() => this.date() === todayVn() && this.days() === 1);
  readonly dateLabel = computed(() => {
    const fmt = (iso: string): string =>
      new Intl.DateTimeFormat('vi-VN', { weekday: 'long', day: 'numeric', month: 'numeric', year: 'numeric' }).format(
        new Date(`${iso}T00:00:00`),
      );
    return this.days() === 1 ? fmt(this.date()) : `${fmt(this.date())} → ${fmt(addDays(this.date(), this.days() - 1))}`;
  });
  /** nhóm theo ngày khi xem 7 ngày */
  readonly groups = computed(() => {
    const map = new Map<string, BookingRow[]>();
    for (const r of this.rows()) {
      const key = new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Ho_Chi_Minh' }).format(new Date(r.startTime));
      map.set(key, [...(map.get(key) ?? []), r]);
    }
    return [...map.entries()].map(([day, items]) => ({ day, items }));
  });
  readonly summary = computed(() => {
    const active = this.rows().filter(r => r.status !== 'CANCELLED' && r.status !== 'NO_SHOW');
    return {
      count: active.length,
      guests: active.reduce((s, r) => s + r.numberOfPeople, 0),
      expected: active.reduce((s, r) => s + (r.totalAmount ?? 0), 0),
      collected: this.rows()
        .filter(r => r.paymentStatus === 'PAID')
        .reduce((s, r) => s + (r.totalAmount ?? 0), 0),
    };
  });

  // ---- tạo nhanh lịch hẹn ----
  readonly createOpen = signal(false);
  readonly form = signal<QuickForm>({
    branchId: null,
    date: todayVn(),
    time: '',
    serviceIds: [],
    people: 1,
    fullName: '',
    phone: '',
    note: '',
    source: 'PHONE',
  });
  readonly formError = signal<string | null>(null);
  readonly saving = signal(false);
  readonly formTotal = computed(() => {
    const f = this.form();
    const sum = this.services()
      .filter(s => f.serviceIds.includes(s.id))
      .reduce((acc, s) => acc + (s.salePrice ?? s.price), 0);
    return sum * f.people;
  });
  readonly formDuration = computed(() =>
    this.services()
      .filter(s => this.form().serviceIds.includes(s.id))
      .reduce((acc, s) => acc + s.durationMinutes, 0),
  );

  private readonly siteApi = inject(SiteApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.siteApi.site().subscribe(site => {
      this.branches.set(site.branches);
      this.services.set(site.services);
      if (!this.form().branchId && site.branches.length) {
        this.patchForm({ branchId: site.branches[0].id });
      }
    });
    this.route.queryParamMap.subscribe(p => {
      this.date.set(p.get('date') ?? todayVn());
      this.days.set(Number(p.get('days')) === 7 ? 7 : 1);
      this.branchId.set(p.get('branch') ? Number(p.get('branch')) : null);
      this.status.set((p.get('status') as BookingStatus | null) ?? null);
      this.q.set(p.get('q') ?? '');
      if (p.get('create')) {
        this.openCreate();
      }
      this.load();
    });
    // tự làm mới mỗi phút
    const timer = window.setInterval(() => !document.hidden && !this.busyId() && this.load(true), 60000);
    this.destroyRef.onDestroy(() => window.clearInterval(timer));
  }

  // ===================== lọc =====================

  setFilter(patch: Record<string, string | number | null>): void {
    void this.router.navigate([], { queryParams: patch, queryParamsHandling: 'merge', replaceUrl: true });
  }

  shiftDate(delta: number): void {
    this.setFilter({ date: addDays(this.date(), delta * this.days()) });
  }

  goToday(): void {
    this.setFilter({ date: todayVn() });
  }

  search(value: string): void {
    this.setFilter({ q: value.trim() || null });
  }

  load(silent = false): void {
    if (!silent) {
      this.loading.set(true);
    }
    this.api.bookings(this.date(), this.days(), this.branchId(), this.status(), this.q()).subscribe({
      next: rows => {
        this.rows.set(rows);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  // ===================== thao tác =====================

  act(row: BookingRow, action: Action): void {
    if (action.confirm && !window.confirm(`${action.confirm}\n${row.customerName ?? ''} – ${this.time(row.startTime)}`)) {
      return;
    }
    this.update(row, action.status, null, null, `Đã chuyển sang "${this.statusText(action.status)}"`);
  }

  complete(row: BookingRow, method: PaymentMethod): void {
    this.payingId.set(null);
    this.update(row, 'COMPLETED', 'PAID', method, 'Đã hoàn thành và ghi nhận thanh toán');
  }

  completeUnpaid(row: BookingRow): void {
    this.payingId.set(null);
    this.update(row, 'COMPLETED', null, null, 'Đã hoàn thành (chưa thu tiền)');
  }

  markPaid(row: BookingRow, method: PaymentMethod): void {
    this.payingId.set(null);
    this.update(row, row.status, 'PAID', method, 'Đã ghi nhận thanh toán');
  }

  // ===================== tạo nhanh =====================

  openCreate(): void {
    this.formError.set(null);
    this.patchForm({ date: this.date() });
    this.createOpen.set(true);
  }

  closeCreate(): void {
    this.createOpen.set(false);
    if (this.route.snapshot.queryParamMap.get('create')) {
      this.setFilter({ create: null });
    }
  }

  patchForm(patch: Partial<QuickForm>): void {
    this.form.update(f => ({ ...f, ...patch }));
  }

  toggleService(id: number): void {
    const ids = this.form().serviceIds;
    this.patchForm({ serviceIds: ids.includes(id) ? ids.filter(x => x !== id) : [...ids, id] });
  }

  submitCreate(): void {
    const f = this.form();
    if (!f.serviceIds.length || !f.time || !f.date || !normalizeVnPhone(f.phone)) {
      this.formError.set('Cần chọn dịch vụ, ngày, giờ và nhập số điện thoại hợp lệ.');
      return;
    }
    this.saving.set(true);
    this.formError.set(null);
    this.api
      .quickCreate(
        {
          branchId: f.branchId,
          serviceIds: f.serviceIds,
          date: f.date,
          time: f.time,
          numberOfPeople: f.people,
          fullName: f.fullName.trim(),
          phone: f.phone.trim(),
          email: null,
          note: f.note.trim() || null,
          quick: false,
          website: '',
        },
        f.source,
      )
      .subscribe({
        next: res => {
          this.saving.set(false);
          this.createOpen.set(false);
          this.form.update(x => ({ ...x, time: '', serviceIds: [], people: 1, fullName: '', phone: '', note: '' }));
          this.flash(`Đã tạo lịch ${res.code}`);
          this.setFilter({ date: f.date, create: null });
          this.load(true);
        },
        error: (err: HttpErrorResponse) => {
          this.saving.set(false);
          const key = ((err.error as { message?: string } | null)?.message ?? '').replace('error.', '');
          const messages: Record<string, string> = {
            slotfull: 'Khung giờ này đã đủ khách theo sức chứa của cơ sở. Chọn giờ khác hoặc cơ sở khác.',
            pasttime: 'Giờ này đã qua quá lâu.',
            pastdate: 'Không tạo lịch cho ngày đã qua.',
            invalidtime: 'Giờ nằm ngoài giờ mở cửa của cơ sở.',
            invalidphone: 'Số điện thoại chưa đúng.',
            servicenotfound: 'Dịch vụ đã chọn không còn hoạt động.',
          };
          this.formError.set(messages[key] ?? 'Không tạo được lịch hẹn. Kiểm tra lại thông tin.');
        },
      });
  }

  // ===================== hiển thị =====================

  time(iso: string | null): string {
    return iso ? formatTime(iso) : '';
  }

  money(v: number | null | undefined): string {
    return formatPrice(v ?? 0, 'vi');
  }

  dayHeading(iso: string): string {
    return new Intl.DateTimeFormat('vi-VN', { weekday: 'long', day: 'numeric', month: 'numeric' }).format(new Date(`${iso}T00:00:00`));
  }

  statusText(s: BookingStatus): string {
    return {
      PENDING: 'Chờ xác nhận',
      CONFIRMED: 'Đã xác nhận',
      IN_PROGRESS: 'Đang phục vụ',
      COMPLETED: 'Hoàn thành',
      CANCELLED: 'Đã hủy',
      NO_SHOW: 'Không đến',
    }[s];
  }

  zalo(phone: string | null): string | null {
    return zaloLink(phone);
  }

  private update(row: BookingRow, status: BookingStatus, pay: 'PAID' | null, method: PaymentMethod | null, message: string): void {
    this.busyId.set(row.id);
    this.api.updateStatus(row.id, status, pay, method).subscribe({
      next: updated => {
        this.rows.update(list => list.map(r => (r.id === updated.id ? updated : r)));
        this.busyId.set(null);
        this.flash(message);
      },
      error: () => {
        this.busyId.set(null);
        this.flash('Không lưu được, vui lòng thử lại.');
      },
    });
  }
  private flash(message: string): void {
    this.toast.set(message);
    window.setTimeout(() => this.toast.set(null), 3000);
  }
}
