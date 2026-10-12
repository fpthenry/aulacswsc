import { Component, DestroyRef, ElementRef, OnInit, computed, effect, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { ImageSet, formatDate, imageSet, lines, localBusinessLd, paragraphs } from '../../core/site-utils';
import { BlogPost, GalleryImage } from '../../core/site.models';
import { CountUpDirective } from '../../shared/count-up.directive';
import { RevealDirective } from '../../shared/reveal.directive';
import { ReviewSliderComponent } from '../../shared/review-slider.component';
import { ServiceCardComponent } from '../../shared/service-card.component';
import { SiteIconComponent } from '../../shared/site-icon.component';
import { SiteImageComponent } from '../../shared/site-image.component';

@Component({
  selector: 'site-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [
    RouterLink,
    TranslateModule,
    RevealDirective,
    ServiceCardComponent,
    SiteIconComponent,
    SiteImageComponent,
    ReviewSliderComponent,
    CountUpDirective,
  ],
})
export default class HomeComponent implements OnInit {
  readonly state = inject(SiteStateService);
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);

  readonly setting = this.state.setting;
  readonly gallery = signal<GalleryImage[]>([]);
  readonly posts = signal<BlogPost[]>([]);
  readonly videoPaused = signal(false);
  readonly reviewCount = signal(0);
  readonly reviewAvg = signal(0);

  // ===================== hiệu ứng các mục bên dưới =====================
  /** Dải số liệu đếm tăng dần — lấy từ dữ liệu thật */
  readonly stats = computed(() =>
    [
      { value: this.state.services().length, decimals: 0, suffix: '', key: 'services' },
      { value: this.state.branches().length, decimals: 0, suffix: '', key: 'branches' },
      { value: this.reviewCount(), decimals: 0, suffix: '+', key: 'reviews' },
      { value: this.reviewAvg(), decimals: 1, suffix: '★', key: 'rating' },
    ].filter(s => s.value > 0),
  );
  /** Dải chữ chạy ngang: tên các liệu trình */
  readonly marquee = computed(() => this.state.services().map(s => this.state.t(s, 'name')));
  /** 2 hàng ảnh trôi ngược chiều khi cuộn */
  readonly galleryRows = computed(() => {
    const imgs = this.gallery();
    return [imgs.filter((_, i) => i % 2 === 0), imgs.filter((_, i) => i % 2 === 1)];
  });
  /** Ảnh nền parallax của khối kêu gọi đặt lịch cuối trang */
  readonly ctaImage = computed(() => {
    const slides = this.slides();
    const gallery = this.gallery();
    if (slides.length) {
      return slides[Math.min(1, slides.length - 1)].src;
    }
    return gallery.length ? gallery[0].imageUrl : null;
  });

  readonly aboutParagraphs = computed(() => paragraphs(this.state.t(this.setting(), 'aboutContent')));
  readonly heroImage = computed(() => imageSet(this.setting().heroImageUrl));

  // ===================== hero slideshow =====================
  private readonly reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  /** Ảnh slideshow (Cài đặt website → Hero Slides, mỗi dòng 1 URL); trống thì dùng Hero Image Url */
  readonly slides = computed<ImageSet[]>(() => {
    const list = lines(this.setting().heroSlides)
      .map(u => imageSet(u))
      .filter((x): x is ImageSet => !!x);
    const single = this.heroImage();
    return list.length ? list : single ? [single] : [];
  });
  readonly slideIndex = signal(0);
  /** tự chuyển ảnh; tắt sẵn khi người dùng bật "giảm chuyển động" */
  readonly playing = signal(!this.reducedMotion);
  /** Slogan tách 2 dòng: dòng sau in nghiêng màu đồng. Admin đặt dấu "|" để chọn chỗ tách, mặc định tách ở " và " / " and ". */
  readonly sloganParts = computed(() => {
    const text = this.state.t(this.setting(), 'slogan') || this.state.tr('site.home.defaultSlogan');
    if (text.includes('|')) {
      const [a, ...b] = text.split('|');
      return { main: a.trim(), accent: b.join(' ').trim() };
    }
    const m = /^(.+?)\s+((?:và|and)\s.+)$/i.exec(text);
    return m ? { main: m[1], accent: m[2] } : { main: text, accent: '' };
  });
  /** Video nền: chỉ khi admin bật, người dùng không bật "giảm chuyển động" và không ở chế độ tiết kiệm dữ liệu. */
  readonly showVideo = computed(() => {
    const s = this.setting();
    if (!s.heroVideoEnabled || !s.heroVideoUrl) {
      return false;
    }
    const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const saveData = (navigator as Navigator & { connection?: { saveData?: boolean } }).connection?.saveData === true;
    return !reduced && !saveData;
  });
  readonly highlights = computed(() =>
    [1, 2, 3].map((i, idx) => ({
      icon: ['pin', 'hand', 'sparkle'][idx],
      title: this.state.t(this.setting(), `highlight${i}Title`),
      text: this.state.t(this.setting(), `highlight${i}Text`),
    })),
  );

  private readonly video = viewChild<ElementRef<HTMLVideoElement>>('heroVideo');
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    // chuyển ảnh mỗi 7 giây (dừng khi tab bị ẩn để đỡ tốn tài nguyên)
    const timer = window.setInterval(() => {
      const n = this.slides().length;
      if (this.playing() && n > 1 && !document.hidden && !this.showVideo()) {
        this.slideIndex.update(i => (i + 1) % n);
      }
    }, 7000);
    this.destroyRef.onDestroy(() => window.clearInterval(timer));
    // hero tối → header trong suốt chữ sáng
    this.destroyRef.onDestroy(this.state.registerDarkHero());

    effect(() => {
      const lang = this.state.lang();
      const s = this.setting();
      const name = this.state.siteName();
      const slogan = this.state.t(s, 'slogan');
      this.seo.set({
        lang,
        path: '',
        title: slogan ? `${name} – ${slogan}` : name,
        description: this.state.t(s, 'metaDescription') || this.state.t(s, 'heroSubtitle'),
        image: s.heroImageUrl,
        jsonLd: localBusinessLd(s, this.state.branches(), location.origin, lang),
      });
    });
  }

  ngOnInit(): void {
    this.api.gallery().subscribe(items => this.gallery.set(items.slice(0, 16)));
    this.api.reviews(0, 50).subscribe(page => {
      this.reviewCount.set(page.total);
      const sum = page.items.reduce((acc, r) => acc + r.rating, 0);
      this.reviewAvg.set(page.items.length ? Math.round((sum / page.items.length) * 10) / 10 : 0);
    });
    this.api.blog(0, 3).subscribe(page => this.posts.set(page.items));
  }

  goToSlide(i: number): void {
    this.slideIndex.set(i);
  }

  /** Nút tạm dừng / phát chuyển động đầu trang (video hoặc slideshow) — yêu cầu WCAG 2.2.2 */
  toggleMotion(): void {
    const el = this.video()?.nativeElement;
    if (!el) {
      this.playing.update(v => !v);
      return;
    }
    if (el.paused) {
      void el.play();
      this.videoPaused.set(false);
    } else {
      el.pause();
      this.videoPaused.set(true);
    }
  }

  readonly motionPaused = computed(() => (this.showVideo() ? this.videoPaused() : !this.playing()));

  date(iso: string | null | undefined): string {
    return formatDate(iso, this.state.lang());
  }
}
