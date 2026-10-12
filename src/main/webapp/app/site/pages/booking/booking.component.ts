import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, computed, effect, inject, input, signal, untracked, viewChild } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { BookingDraftService, unitPrice } from '../../core/booking-draft.service';
import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import {
  EMAIL_PATTERN,
  addDays,
  bookingIcs,
  downloadFile,
  formatDate,
  formatDuration,
  formatPrice,
  formatTime,
  normalizeVnPhone,
  todayVn,
  whatsappLink,
  zaloLink,
} from '../../core/site-utils';
import { BookingConfirmation, TimeSlot } from '../../core/site.models';
import { SiteIconComponent } from '../../shared/site-icon.component';

/** Các bước của luồng đặt lịch đầy đủ. */
const STEPS = ['branch', 'services', 'date', 'time', 'people', 'info', 'confirm'] as const;
type StepKey = (typeof STEPS)[number];
const MAX_DAYS_AHEAD = 60;
const MAX_PEOPLE = 20;

interface CalendarDay {
  iso: string;
  day: number;
  disabled: boolean;
  today: boolean;
}

@Component({
  selector: 'site-booking',
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.scss',
  imports: [PageHeroComponent, RouterLink, TranslateModule, SiteIconComponent],
})
export default class BookingComponent {
  /** ?mode=quick → đặt nhanh; ?branch=slug → chọn sẵn cơ sở */
  readonly mode = input<string>();
  readonly branch = input<string>();

  readonly state = inject(SiteStateService);
  readonly booking = inject(BookingDraftService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);
  private readonly router = inject(Router);

  readonly steps = STEPS;
  readonly maxPeople = MAX_PEOPLE;
  readonly d = this.booking.draft;
  readonly quick = computed(() => this.mode() === 'quick');

  readonly today = todayVn();
  readonly maxDate = addDays(this.today, MAX_DAYS_AHEAD);

  // ---- khung giờ ----
  readonly slots = signal<TimeSlot[]>([]);
  readonly slotsLoading = signal(false);
  readonly slotsError = signal(false);
  private slotsSub?: Subscription;

  // ---- gửi ----
  readonly submitting = signal(false);
  readonly submitError = signal<string | null>(null);
  readonly result = signal<BookingConfirmation | null>(null);
  readonly showErrors = signal(false);
  readonly honeypot = signal('');
  readonly copied = signal(false);

  // ---- lịch ----
  readonly viewMonth = signal(this.today.slice(0, 7));

  private readonly stepHeading = viewChild<ElementRef<HTMLElement>>('stepHeading');

  /** Cơ sở dùng để tính giờ: đã chọn, hoặc cơ sở đầu tiên (đặt nhanh / chỉ có 1 cơ sở). */
  readonly branchId = computed(
    () => this.d().branchId ?? (this.quick() || this.state.branches().length === 1 ? (this.state.branches()[0]?.id ?? null) : null),
  );
  readonly selectedBranch = computed(() => this.state.branches().find(b => b.id === this.branchId()));
  readonly duration = computed(() => this.booking.totalDuration());

  readonly visibleSlots = computed(() => {
    // hôm nay: ẩn giờ đã qua; ngày khác: hiện cả giờ đã đầy (bị khóa)
    return this.slots().filter(s => s.status !== 'PAST');
  });
  readonly selectedSlotOk = computed(() => {
    const time = this.d().time;
    return !!time && this.slots().some(s => s.time === time && s.status === 'AVAILABLE');
  });

  // ---- kiểm tra từng bước ----
  readonly phoneOk = computed(() => normalizeVnPhone(this.d().phone) !== null);
  readonly emailOk = computed(() => !this.d().email.trim() || EMAIL_PATTERN.test(this.d().email.trim()));
  readonly nameOk = computed(() => this.d().fullName.trim().length >= 2);
  readonly dateOk = computed(() => {
    const date = this.d().date;
    return !!date && date >= this.today && date <= this.maxDate;
  });
  readonly valid = computed<Record<StepKey, boolean>>(() => ({
    branch: this.branchId() !== null,
    services: this.booking.count() > 0,
    date: this.dateOk(),
    time: this.selectedSlotOk(),
    people: this.d().people >= 1 && this.d().people <= MAX_PEOPLE && this.selectedSlotOk(),
    info: this.nameOk() && this.phoneOk() && this.emailOk(),
    confirm: true,
  }));
  /** Bước xa nhất được phép mở = bước đầu tiên chưa hợp lệ. */
  readonly maxReachable = computed(() => {
    const v = this.valid();
    const firstInvalid = STEPS.findIndex(s => !v[s]);
    return firstInvalid === -1 ? STEPS.length : firstInvalid + 1;
  });
  readonly step = computed(() => Math.min(Math.max(this.d().step, 1), this.maxReachable()));
  readonly stepKey = computed<StepKey>(() => STEPS[this.step() - 1]);

  readonly quickValid = computed(
    () => this.branchId() !== null && this.booking.count() > 0 && this.dateOk() && this.selectedSlotOk() && this.phoneOk(),
  );

  readonly calendar = computed(() => this.buildMonth(this.viewMonth()));
  readonly monthLabel = computed(() =>
    new Intl.DateTimeFormat(this.state.lang() === 'vi' ? 'vi-VN' : 'en-GB', { month: 'long', year: 'numeric' }).format(
      new Date(`${this.viewMonth()}-01T00:00:00`),
    ),
  );
  readonly weekdays = computed(() => {
    const fmt = new Intl.DateTimeFormat(this.state.lang() === 'vi' ? 'vi-VN' : 'en-GB', { weekday: 'short' });
    // 2024-01-01 là thứ Hai
    return Array.from({ length: 7 }, (_, i) => fmt.format(new Date(2024, 0, 1 + i)));
  });
  readonly canPrevMonth = computed(() => this.viewMonth() > this.today.slice(0, 7));
  readonly canNextMonth = computed(() => this.viewMonth() < this.maxDate.slice(0, 7));

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'booking',
        title: `${this.state.tr('site.booking.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.booking.metaDescription'),
      });
    });

    // ?branch=slug → chọn sẵn cơ sở
    effect(() => {
      const slug = this.branch();
      const found = this.state.branches().find(b => b.slug === slug);
      if (found && untracked(() => this.d().branchId) !== found.id) {
        untracked(() => this.booking.update({ branchId: found.id, time: null }));
      }
    });

    // Tải khung giờ khi cơ sở / ngày / thời lượng / số người thay đổi
    effect(() => {
      const branchId = this.branchId();
      const date = this.d().date;
      const duration = this.duration();
      const people = this.quick() ? 1 : this.d().people;
      untracked(() => this.loadSlots(branchId, date, duration, people));
    });

    // Chuyển bước → đưa focus tới tiêu đề bước (trình đọc màn hình đọc được bước mới)
    let firstRun = true;
    effect(() => {
      this.step();
      if (firstRun) {
        firstRun = false; // lần mở trang đầu tiên: không cướp focus
        return;
      }
      const el = untracked(() => this.stepHeading()?.nativeElement);
      if (el && untracked(() => !this.result())) {
        queueMicrotask(() => el.focus());
      }
    });

    if (this.d().date) {
      this.viewMonth.set(this.d().date!.slice(0, 7));
    }
  }

  // ===================== điều hướng bước =====================

  goTo(step: number): void {
    if (step >= 1 && step <= this.maxReachable()) {
      this.showErrors.set(false);
      this.booking.update({ step });
    }
  }

  next(): void {
    if (!this.valid()[this.stepKey()]) {
      this.showErrors.set(true);
      return;
    }
    this.goTo(this.step() + 1);
  }

  back(): void {
    this.goTo(this.step() - 1);
  }

  // ===================== chọn dữ liệu =====================

  chooseBranch(id: number): void {
    this.booking.update({ branchId: id, time: null });
  }

  chooseDate(iso: string): void {
    this.booking.update({ date: iso, time: null });
  }

  chooseTime(slot: TimeSlot): void {
    if (slot.status === 'AVAILABLE') {
      this.booking.update({ time: slot.time });
    }
  }

  setPeople(n: number): void {
    const people = Math.min(Math.max(Math.round(n) || 1, 1), MAX_PEOPLE);
    this.booking.update({ people });
  }

  field(name: 'fullName' | 'phone' | 'email' | 'note', value: string): void {
    this.booking.update({ [name]: value });
  }

  shiftMonth(delta: number): void {
    const [y, m] = this.viewMonth().split('-').map(Number);
    const d = new Date(Date.UTC(y, m - 1 + delta, 1));
    this.viewMonth.set(d.toISOString().slice(0, 7));
  }

  // ===================== gửi =====================

  submit(): void {
    if (this.quick() ? !this.quickValid() : this.maxReachable() < STEPS.length) {
      this.showErrors.set(true);
      return;
    }
    const d = this.d();
    this.submitting.set(true);
    this.submitError.set(null);
    this.api
      .book({
        branchId: this.branchId(),
        serviceIds: d.serviceIds,
        date: d.date!,
        time: d.time!,
        numberOfPeople: this.quick() ? 1 : d.people,
        fullName: this.quick() ? d.fullName.trim() || '' : d.fullName.trim(),
        phone: d.phone.trim(),
        email: this.quick() ? null : d.email.trim() || null,
        note: this.quick() ? null : d.note.trim() || null,
        quick: this.quick(),
        website: this.honeypot(),
      })
      .subscribe({
        next: res => {
          this.submitting.set(false);
          this.result.set(res);
          // đặt thành công → xóa nháp (không giữ thông tin cá nhân trên máy)
          this.booking.clear();
          window.scrollTo({ top: 0 });
        },
        error: (err: HttpErrorResponse) => {
          this.submitting.set(false);
          const key = this.errorKey(err);
          this.submitError.set(key);
          if (key === 'slotfull' || key === 'pasttime') {
            // giờ vừa bị người khác đặt → tải lại giờ và đưa khách về bước chọn giờ
            this.booking.update({ time: null });
            this.loadSlots(this.branchId(), this.d().date, this.duration(), this.quick() ? 1 : this.d().people);
            if (!this.quick()) {
              this.booking.update({ step: 4 });
            }
          }
        },
      });
  }

  newBooking(): void {
    this.result.set(null);
    void this.router.navigate(this.state.link('booking'));
  }

  // ===================== sau khi đặt =====================

  downloadIcs(): void {
    const r = this.result();
    if (r) {
      downloadFile(bookingIcs(r, this.state.lang(), this.state.siteName()), `${r.code}.ics`, 'text/calendar;charset=utf-8');
    }
  }

  shareText(): string {
    const r = this.result();
    if (!r) {
      return '';
    }
    const lang = this.state.lang();
    const services = r.services.map(s => (lang === 'vi' ? s.nameVi : s.nameEn || s.nameVi)).join(', ');
    return this.state.tr('site.booking.shareText', {
      code: r.code,
      name: r.customerName,
      services,
      time: `${formatTime(r.startTime)} ${formatDate(r.startTime, lang)}`,
      people: r.numberOfPeople,
      branch: lang === 'vi' ? r.branch.nameVi : r.branch.nameEn,
    });
  }

  whatsappShare(): string | null {
    const r = this.result();
    return whatsappLink(this.state.setting().whatsappNumber ?? r?.branch.phone, this.shareText());
  }

  /** Zalo không hỗ trợ điền sẵn nội dung qua link → sao chép nội dung rồi mở Zalo của spa. */
  shareZalo(): void {
    const text = this.shareText();
    const open = (): void => {
      const link = zaloLink(this.state.setting().zaloNumber ?? this.result()?.branch.phone);
      if (link) {
        window.open(link, '_blank', 'noopener');
      }
    };
    try {
      navigator.clipboard
        .writeText(text)
        .then(() => {
          this.copied.set(true);
          open();
        })
        .catch(open);
    } catch {
      // trang không chạy HTTPS → không có clipboard API
      open();
    }
  }

  // ===================== hiển thị =====================

  price(v: number | null | undefined): string {
    return formatPrice(v, this.state.lang());
  }

  minutes(v: number): string {
    return formatDuration(v, this.state.lang());
  }

  longDate(iso: string | null | undefined): string {
    return formatDate(iso, this.state.lang(), true);
  }

  time(iso: string): string {
    return formatTime(iso);
  }

  unit = unitPrice;

  private loadSlots(branchId: number | null, date: string | null, duration: number, people: number): void {
    this.slotsSub?.unsubscribe();
    this.slotsError.set(false);
    if (!branchId || !date || duration <= 0 || date < this.today) {
      this.slots.set([]);
      return;
    }
    this.slotsLoading.set(true);
    this.slotsSub = this.api.availability(branchId, date, duration, people).subscribe({
      next: slots => {
        this.slots.set(slots);
        this.slotsLoading.set(false);
        // giờ đã chọn không còn trống (đổi số người / dịch vụ) → bỏ chọn
        const time = this.d().time;
        if (time && !slots.some(s => s.time === time && s.status === 'AVAILABLE')) {
          this.booking.update({ time: null });
        }
      },
      error: () => {
        this.slots.set([]);
        this.slotsLoading.set(false);
        this.slotsError.set(true);
      },
    });
  }

  private buildMonth(month: string): (CalendarDay | null)[] {
    const [y, m] = month.split('-').map(Number);
    const first = new Date(Date.UTC(y, m - 1, 1));
    const daysInMonth = new Date(Date.UTC(y, m, 0)).getUTCDate();
    // thứ Hai = cột đầu
    const offset = (first.getUTCDay() + 6) % 7;
    const cells: (CalendarDay | null)[] = Array.from({ length: offset }, () => null);
    for (let day = 1; day <= daysInMonth; day++) {
      const iso = `${month}-${String(day).padStart(2, '0')}`;
      cells.push({ iso, day, disabled: iso < this.today || iso > this.maxDate, today: iso === this.today });
    }
    return cells;
  }

  private errorKey(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'network';
    }
    if (err.status === 429) {
      return 'tooMany';
    }
    const message = (err.error as { message?: string } | null)?.message ?? '';
    const key = message.startsWith('error.') ? message.slice(6) : '';
    const known = [
      'slotfull',
      'pasttime',
      'pastdate',
      'datetoofar',
      'invalidphone',
      'namerequired',
      'servicenotfound',
      'branchnotfound',
      'invalidtime',
    ];
    if (known.includes(key)) {
      return key;
    }
    return err.status === 400 ? 'validation' : 'server';
  }
}
