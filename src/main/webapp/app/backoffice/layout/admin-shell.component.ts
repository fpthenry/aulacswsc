import { Component, DestroyRef, ViewEncapsulation, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { SiteIconComponent } from 'app/site/shared/site-icon.component';
import { BackofficeApiService, Badges } from '../backoffice-api.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
  badge?: keyof Badges;
  exact?: boolean;
}
interface MenuGroup {
  title: string | null;
  items: MenuItem[];
}

const ADMIN = 'ROLE_ADMIN';
const STAFF = 'ROLE_STAFF';

/** Menu trang quản trị — thêm mục mới ở đây. */
const MENU: MenuGroup[] = [
  { title: null, items: [{ label: 'Tổng quan', icon: 'grid', route: '/dashboard', roles: [ADMIN, STAFF], exact: true }] },
  {
    title: 'Lịch hẹn',
    items: [
      { label: 'Bảng lịch hẹn', icon: 'calendar', route: '/booking-board', roles: [ADMIN, STAFF], badge: 'pendingBookings' },
      { label: 'Khách hàng', icon: 'users', route: '/customer', roles: [ADMIN, STAFF] },
      { label: 'Khuyến mãi', icon: 'tag', route: '/promotion', roles: [ADMIN] },
    ],
  },
  {
    title: 'Nội dung website',
    items: [
      { label: 'Cài đặt website', icon: 'settings', route: '/cai-dat-website', roles: [ADMIN] },
      { label: 'Cơ sở', icon: 'pin', route: '/branch', roles: [ADMIN] },
      { label: 'Danh mục dịch vụ', icon: 'list', route: '/service-category', roles: [ADMIN] },
      { label: 'Dịch vụ & bảng giá', icon: 'sparkle', route: '/spa-service', roles: [ADMIN] },
      { label: 'Thư viện ảnh', icon: 'image', route: '/gallery-image', roles: [ADMIN] },
      { label: 'Bài viết / Tin tức', icon: 'file', route: '/blog-post', roles: [ADMIN] },
      { label: 'Đánh giá', icon: 'star', route: '/review', roles: [ADMIN], badge: 'pendingReviews' },
    ],
  },
  {
    title: 'Nhân sự & liên hệ',
    items: [
      { label: 'Nhân viên', icon: 'user', route: '/staff', roles: [ADMIN] },
      { label: 'Tin nhắn liên hệ', icon: 'inbox', route: '/contact-message', roles: [ADMIN], badge: 'unhandledMessages' },
    ],
  },
  {
    title: 'Hệ thống',
    items: [
      { label: 'Tài khoản đăng nhập', icon: 'key', route: '/admin/user-management', roles: [ADMIN] },
      { label: 'Tình trạng hệ thống', icon: 'activity', route: '/admin/health', roles: [ADMIN] },
    ],
  },
];

/**
 * Khung trang quản trị: menu bên trái theo nhóm (có huy hiệu số việc cần xử lý), thanh trên cùng, vùng nội dung.
 * Style dùng chung cho các trang quản trị nằm trong admin-shell.component.scss (không đóng gói).
 */
@Component({
  selector: 'jhi-admin-shell',
  templateUrl: './admin-shell.component.html',
  styleUrl: './admin-shell.component.scss',
  encapsulation: ViewEncapsulation.None,
  imports: [RouterLink, RouterLinkActive, SiteIconComponent],
  host: {
    '(document:keydown)': 'onKeydown($event)',
    '(window:beforeunload)': 'onBeforeUnload($event)',
  },
})
export class AdminShellComponent {
  readonly account = inject(AccountService).trackCurrentAccount();
  readonly api = inject(BackofficeApiService);
  readonly navOpen = signal(false);
  readonly userMenu = signal(false);

  readonly menu = computed(() => {
    const acc = this.account();
    const roles = acc?.authorities ?? [];
    return MENU.map(g => ({ ...g, items: g.items.filter(i => i.roles.some(r => roles.includes(r))) })).filter(g => g.items.length);
  });
  readonly isStaffOrAdmin = computed(() => (this.account()?.authorities ?? []).some(r => r === ADMIN || r === STAFF));
  readonly displayName = computed(() => {
    const a = this.account();
    return a ? [a.lastName, a.firstName].filter(Boolean).join(' ') || a.login : '';
  });
  readonly roleLabel = computed(() => {
    const roles = this.account()?.authorities ?? [];
    return roles.includes(ADMIN) ? 'Quản lý' : roles.includes(STAFF) ? 'Nhân viên' : 'Khách hàng';
  });
  readonly initials = computed(() =>
    this.displayName()
      .split(/\s+/)
      .filter(Boolean)
      .slice(-2)
      .map(w => w[0].toUpperCase())
      .join(''),
  );

  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);

  constructor() {
    // đóng menu (mobile) khi chuyển trang; làm mới huy hiệu mỗi khi đổi trang và mỗi 60 giây
    const sub = this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.navOpen.set(false);
      this.userMenu.set(false);
      if (this.isStaffOrAdmin()) {
        this.api.refreshBadges();
      }
    });
    const timer = window.setInterval(() => {
      if (this.isStaffOrAdmin() && !document.hidden) {
        this.api.refreshBadges();
      }
    }, 60000);
    // bấm link menu khi form đang sửa dở: hỏi lại trước khi rời trang (bắt ở pha capture, trước routerLink)
    const guardLinks = (e: MouseEvent): void => {
      const link = (e.target as HTMLElement | null)?.closest('a[href]');
      if (link && !link.closest('form') && this.hasUnsavedForm() && !window.confirm('Bạn có thay đổi chưa lưu. Rời trang này?')) {
        e.preventDefault();
        e.stopImmediatePropagation();
      }
    };
    document.addEventListener('click', guardLinks, true);
    inject(DestroyRef).onDestroy(() => {
      sub.unsubscribe();
      window.clearInterval(timer);
      document.removeEventListener('click', guardLinks, true);
    });
    if (this.isStaffOrAdmin()) {
      this.api.refreshBadges();
    }
  }

  badge(item: MenuItem): number {
    return item.badge ? this.api.badges()[item.badge] : 0;
  }

  /** Ctrl+S / Cmd+S: lưu form đang mở (bấm nút Lưu nếu đang bật). */
  onKeydown(e: KeyboardEvent): void {
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
      const save = document.querySelector<HTMLButtonElement>('form[name="editForm"] #save-entity');
      if (save) {
        e.preventDefault();
        if (!save.disabled) {
          save.click();
        }
      }
    }
  }

  /** Đóng tab / tải lại khi form đang sửa dở: trình duyệt hỏi lại. */
  onBeforeUnload(e: BeforeUnloadEvent): void {
    if (this.hasUnsavedForm()) {
      e.preventDefault();
    }
  }

  logout(): void {
    this.loginService.logout();
    void this.router.navigate(['']);
  }

  private hasUnsavedForm(): boolean {
    return document.querySelector('form[name="editForm"].ng-dirty') !== null;
  }
}
