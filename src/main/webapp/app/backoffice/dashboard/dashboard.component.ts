import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { formatPrice, formatTime } from 'app/site/core/site-utils';
import { SiteIconComponent } from 'app/site/shared/site-icon.component';
import { BackofficeApiService, Dashboard } from '../backoffice-api.service';

/**
 * Tổng quan: số liệu hôm nay, việc cần xử lý, lịch hôm nay, 7 ngày tới, dịch vụ đặt nhiều, lối tắt.
 */
@Component({
  selector: 'jhi-backoffice-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  imports: [RouterLink, TranslateModule, SiteIconComponent],
})
export default class DashboardComponent implements OnInit {
  readonly api = inject(BackofficeApiService);
  readonly account = inject(AccountService).trackCurrentAccount();

  readonly data = signal<Dashboard | null>(null);
  readonly loading = signal(true);
  readonly error = signal(false);

  readonly isAdmin = computed(() => (this.account()?.authorities ?? []).includes('ROLE_ADMIN'));
  readonly greeting = computed(() => {
    const h = new Date().getHours();
    const name = this.account()?.firstName ?? this.account()?.login ?? '';
    return `${h < 11 ? 'Chào buổi sáng' : h < 14 ? 'Chào buổi trưa' : h < 18 ? 'Chào buổi chiều' : 'Chào buổi tối'}${name ? ', ' + name : ''}`;
  });
  readonly today = new Intl.DateTimeFormat('vi-VN', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }).format(new Date());
  readonly todos = computed(() => {
    const d = this.data();
    if (!d) {
      return [];
    }
    return [
      {
        count: d.pendingBookings,
        text: 'lịch hẹn chờ xác nhận',
        link: '/booking-board',
        params: { status: 'PENDING', days: 7 },
        icon: 'calendar',
      },
      { count: d.unhandledMessages, text: 'tin nhắn liên hệ chưa xử lý', link: '/contact-message', params: {}, icon: 'inbox' },
      { count: d.pendingReviews, text: 'đánh giá chờ duyệt', link: '/review', params: {}, icon: 'star' },
    ].filter(t => t.count > 0);
  });
  readonly maxDay = computed(() => Math.max(1, ...(this.data()?.next7Days ?? []).map(d => d.bookings)));
  readonly maxService = computed(() => Math.max(1, ...(this.data()?.topServices ?? []).map(s => s.count)));

  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.load();
    // tự làm mới mỗi 2 phút (lịch mới từ website hiện lên mà không cần tải lại trang)
    const timer = window.setInterval(() => !document.hidden && this.load(true), 120000);
    this.destroyRef.onDestroy(() => window.clearInterval(timer));
  }

  load(silent = false): void {
    if (!silent) {
      this.loading.set(true);
    }
    this.api.dashboard().subscribe({
      next: d => {
        this.data.set(d);
        this.loading.set(false);
        this.error.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set(true);
      },
    });
  }

  money(v: number | null | undefined): string {
    return formatPrice(v ?? 0, 'vi');
  }

  time(iso: string): string {
    return formatTime(iso);
  }

  dayLabel(iso: string, index: number): string {
    if (index === 0) {
      return 'Hôm nay';
    }
    // nhãn ngắn để vừa cột: "T3 13", "CN 18"
    const d = new Date(`${iso}T00:00:00`);
    return `${d.getDay() === 0 ? 'CN' : `T${d.getDay() + 1}`} ${d.getDate()}`;
  }
}
