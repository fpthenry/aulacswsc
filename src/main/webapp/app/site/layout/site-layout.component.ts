import { Component, ElementRef, OnDestroy, ViewEncapsulation, computed, effect, inject, input, signal, viewChild } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';

import { BookingDraftService } from '../core/booking-draft.service';
import { SiteLang, isSiteLang } from '../core/site-lang';
import { Branch } from '../core/site.models';
import { SiteStateService } from '../core/site-state.service';
import { telLink, whatsappLink, zaloLink } from '../core/site-utils';
import { SiteIconComponent } from '../shared/site-icon.component';

type Theme = 'light' | 'dark' | null;
const THEME_KEY = 'site.theme';
const MUSIC_KEY = 'site.music';

/**
 * Khung website khách: header cố định, menu, chuyển VI/EN, chế độ sáng/tối, nhạc nền, footer, nút liên hệ nổi.
 */
@Component({
  selector: 'site-layout',
  templateUrl: './site-layout.component.html',
  styleUrls: ['./site.scss', './site-layout.component.scss'],
  // design system dùng chung cho mọi trang con (selector đều nằm dưới .site)
  encapsulation: ViewEncapsulation.None,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TranslateModule, SiteIconComponent],
})
export default class SiteLayoutComponent implements OnDestroy {
  /** tham số :lang trên URL (withComponentInputBinding) */
  readonly lang = input<string>();

  readonly state = inject(SiteStateService);
  readonly draft = inject(BookingDraftService);
  private readonly router = inject(Router);

  readonly menuOpen = signal(false);
  readonly scrolled = signal(false);
  readonly theme = signal<Theme>(readStorage(THEME_KEY) as Theme);
  readonly musicOn = signal(false);
  readonly year = new Date().getFullYear();

  readonly setting = this.state.setting;
  readonly hotline = computed(() => this.setting().hotline ?? (this.state.branches()[0] as Branch | undefined)?.phone ?? null);
  readonly telHref = computed(() => telLink(this.hotline()));
  readonly whatsappHref = computed(() => whatsappLink(this.setting().whatsappNumber));
  readonly zaloHref = computed(() => zaloLink(this.setting().zaloNumber));
  readonly otherLang = computed<SiteLang>(() => (this.state.lang() === 'vi' ? 'en' : 'vi'));
  readonly otherLangUrl = signal('/en');

  readonly nav: { key: string; path: string[]; fragment?: string }[] = [
    { key: 'about', path: [], fragment: 'about' },
    { key: 'services', path: ['services'] },
    { key: 'branches', path: ['branches'] },
    { key: 'reviews', path: ['reviews'] },
    { key: 'blog', path: ['blog'] },
    { key: 'gallery', path: ['gallery'] },
  ];

  private readonly sentinel = viewChild<ElementRef<HTMLElement>>('sentinel');
  private readonly audio = viewChild<ElementRef<HTMLAudioElement>>('audio');
  private observer?: IntersectionObserver;

  constructor() {
    effect(() => {
      const lang = this.lang();
      if (isSiteLang(lang)) {
        this.state.setLang(lang);
      }
    });

    this.updateOtherLangUrl(this.router.url);
    this.router.events.pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd)).subscribe(e => {
      this.menuOpen.set(false);
      this.updateOtherLangUrl(e.urlAfterRedirects);
    });

    // header đổi nền khi cuộn: theo dõi 1 phần tử mốc ở đầu trang thay vì lắng nghe scroll
    effect(() => {
      const el = this.sentinel()?.nativeElement;
      if (el && !this.observer && typeof IntersectionObserver !== 'undefined') {
        this.observer = new IntersectionObserver(([entry]) => this.scrolled.set(!entry.isIntersecting));
        this.observer.observe(el);
      }
    });

    effect(() => {
      const theme = this.theme();
      writeStorage(THEME_KEY, theme);
    });

    effect(() => {
      // khi có link nhạc và khách đã từng bật → thử phát lại (trình duyệt có thể chặn tới khi khách tương tác)
      const el = this.audio()?.nativeElement;
      if (el && this.setting().backgroundMusicUrl && readStorage(MUSIC_KEY) === 'on' && !this.musicOn()) {
        el.play()
          .then(() => this.musicOn.set(true))
          .catch(() => this.musicOn.set(false));
      }
    });
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  toggleMenu(): void {
    this.menuOpen.update(v => !v);
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  /** Vòng: theo hệ thống → sáng → tối */
  toggleTheme(): void {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const effective = this.theme() ?? (prefersDark ? 'dark' : 'light');
    this.theme.set(effective === 'dark' ? 'light' : 'dark');
  }

  readonly isDark = computed(() => {
    const t = this.theme();
    return t ? t === 'dark' : typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches;
  });

  toggleMusic(): void {
    const el = this.audio()?.nativeElement;
    if (!el) {
      return;
    }
    if (this.musicOn()) {
      el.pause();
      this.musicOn.set(false);
      writeStorage(MUSIC_KEY, 'off');
    } else {
      el.volume = 0.35;
      el.play()
        .then(() => {
          this.musicOn.set(true);
          writeStorage(MUSIC_KEY, 'on');
        })
        .catch(() => this.musicOn.set(false));
    }
  }

  private updateOtherLangUrl(url: string): void {
    this.otherLangUrl.set(url.replace(/^\/(vi|en)(?=\/|\?|#|$)/, (_m, cur: string) => (cur === 'vi' ? '/en' : '/vi')));
  }

  switchLang(event: Event): void {
    event.preventDefault();
    const url = this.router.url.replace(/^\/(vi|en)(?=\/|\?|#|$)/, `/${this.otherLang()}`);
    this.router.navigateByUrl(url);
  }
}

function readStorage(key: string): string | null {
  try {
    return localStorage.getItem(key);
  } catch {
    return null;
  }
}

function writeStorage(key: string, value: string | null): void {
  try {
    if (value === null) {
      localStorage.removeItem(key);
    } else {
      localStorage.setItem(key, value);
    }
  } catch {
    // bỏ qua
  }
}
