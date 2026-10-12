import { Injectable, computed, inject, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, tap } from 'rxjs';

import { SiteApiService } from './site-api.service';
import { DEFAULT_SITE_LANG, SiteLang, pick, rememberSiteLang } from './site-lang';
import { SiteBootstrap, SpaService } from './site.models';

/**
 * Trạng thái dùng chung của website khách: ngôn ngữ hiện tại + dữ liệu tải từ /api/public/site.
 */
@Injectable({ providedIn: 'root' })
export class SiteStateService {
  private readonly api = inject(SiteApiService);
  private readonly translate = inject(TranslateService);

  readonly lang = signal<SiteLang>(DEFAULT_SITE_LANG);
  readonly data = signal<SiteBootstrap | null>(null);
  readonly loadError = signal(false);

  readonly setting = computed(() => this.data()?.setting ?? {});
  readonly branches = computed(() => this.data()?.branches ?? []);
  readonly categories = computed(() => this.data()?.categories ?? []);
  readonly services = computed(() => this.data()?.services ?? []);
  readonly featuredServices = computed(() => {
    const featured = this.services().filter(s => s.featured);
    return (featured.length ? featured : this.services()).slice(0, 6);
  });
  readonly siteName = computed(() => this.setting().siteName ?? 'Spa');

  /** số khối đầu trang nền tối đang hiển thị (trang chủ, page-hero) → header trong suốt chữ sáng */
  private readonly darkHeroCount = signal(0);
  readonly darkHero = computed(() => this.darkHeroCount() > 0);

  /** Ảnh từ Hero Slides để làm nền đầu trang cho các trang con (mỗi trang một ảnh khác nhau). */
  heroImageFor(page: 'services' | 'reviews' | 'booking' | 'gallery' | 'blog' | 'branches'): string | null {
    const s = this.setting();
    const slides = (s.heroSlides ?? '')
      .split(/\r?\n/)
      .map(u => u.trim())
      .filter(Boolean);
    if (!slides.length) {
      return s.heroImageUrl ?? null;
    }
    const order = ['services', 'reviews', 'booking', 'gallery', 'blog', 'branches'];
    return slides[(order.indexOf(page) + 1) % slides.length];
  }

  registerDarkHero(): () => void {
    this.darkHeroCount.update(n => n + 1);
    return () => this.darkHeroCount.update(n => Math.max(0, n - 1));
  }

  /** tăng mỗi khi bản dịch tải xong → các effect/computed gọi tr() tự cập nhật */
  private readonly i18nTick = signal(0);

  constructor() {
    this.translate.onLangChange.subscribe(() => this.i18nTick.update(v => v + 1));
  }

  /** Dịch chuỗi giao diện (i18n/{vi,en}/site.json) trong code TS, phản ứng theo ngôn ngữ. */
  tr(key: string, params?: Record<string, unknown>): string {
    this.i18nTick();
    this.lang();
    return this.translate.instant(key, params) as string;
  }

  load(): Observable<SiteBootstrap> {
    return this.api.site().pipe(
      tap({
        next: d => this.data.set(d),
        error: () => this.loadError.set(true),
      }),
    );
  }

  setLang(lang: SiteLang): void {
    if (this.lang() !== lang || this.translate.currentLang !== lang) {
      this.lang.set(lang);
      this.translate.use(lang);
      rememberSiteLang(lang);
      document.documentElement.lang = lang;
    }
  }

  /** Lấy trường song ngữ theo ngôn ngữ hiện tại. */
  t(obj: object | null | undefined, field: string): string {
    return pick(obj, field, this.lang());
  }

  /** Đường dẫn trong website, luôn có tiền tố ngôn ngữ: link('services', slug) → ['/', 'vi', 'services', slug]. */
  link(...parts: (string | number)[]): (string | number)[] {
    return ['/', this.lang(), ...parts];
  }

  /** Như link() nhưng nhận mảng (template Angular không hỗ trợ cú pháp ...spread). */
  path(parts: (string | number)[]): (string | number)[] {
    return ['/', this.lang(), ...parts];
  }

  serviceBySlug(slug: string): SpaService | undefined {
    return this.services().find(s => s.slug === slug);
  }

  serviceById(id: number): SpaService | undefined {
    return this.services().find(s => s.id === id);
  }
}
