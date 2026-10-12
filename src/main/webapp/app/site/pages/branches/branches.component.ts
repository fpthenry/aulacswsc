import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { PageHeroComponent } from '../../shared/page-hero.component';

import { SeoService } from '../../core/seo.service';
import { SiteApiService } from '../../core/site-api.service';
import { SiteStateService } from '../../core/site-state.service';
import { EMAIL_PATTERN, localBusinessLd, normalizeVnPhone, telLink, whatsappLink, zaloLink } from '../../core/site-utils';
import { Branch } from '../../core/site.models';
import { RevealDirective } from '../../shared/reveal.directive';
import { SiteIconComponent } from '../../shared/site-icon.component';
import { SiteImageComponent } from '../../shared/site-image.component';

/** Chỉ cho nhúng bản đồ từ Google Maps (khớp CSP frame-src). */
const MAP_PATTERN = /^https:\/\/(www\.)?google\.com\/maps|^https:\/\/maps\.google\.com/;

@Component({
  selector: 'site-branches',
  templateUrl: './branches.component.html',
  styleUrl: './branches.component.scss',
  imports: [PageHeroComponent, RouterLink, TranslateModule, RevealDirective, SiteIconComponent, SiteImageComponent],
})
export default class BranchesComponent {
  readonly state = inject(SiteStateService);
  /** ảnh đầu trang: ảnh cơ sở đầu tiên, không có thì lấy từ Hero Slides */
  readonly heroImage = computed(() => this.state.branches().find(b => b.imageUrl)?.imageUrl ?? this.state.heroImageFor('branches'));
  private readonly api = inject(SiteApiService);
  private readonly seo = inject(SeoService);
  private readonly sanitizer = inject(DomSanitizer);

  /** cơ sở đang chọn để hiện trên bản đồ (mặc định cơ sở đầu tiên) */
  readonly selectedId = signal<number | null>(null);
  readonly selected = computed(() => {
    const list = this.state.branches();
    return list.find(b => b.id === this.selectedId()) ?? (list.length ? list[0] : null);
  });
  /** iframe Google Maps khá nặng → chỉ tải khi khách bấm "Xem bản đồ"; đã tải thì đổi cơ sở sẽ đổi bản đồ luôn */
  readonly mapLoaded = signal(false);

  // ---- form liên hệ ----
  readonly form = signal({ fullName: '', phone: '', email: '', message: '' });
  readonly website = signal('');
  readonly sending = signal(false);
  readonly sent = signal(false);
  readonly sendError = signal<string | null>(null);
  readonly showErrors = signal(false);

  constructor() {
    effect(() => {
      const lang = this.state.lang();
      this.seo.set({
        lang,
        path: 'branches',
        title: `${this.state.tr('site.branches.title')} | ${this.state.siteName()}`,
        description: this.state.tr('site.branches.metaDescription'),
        jsonLd: localBusinessLd(this.state.setting(), this.state.branches(), location.origin, lang),
      });
    });
  }

  /** Tạo SafeResourceUrl một lần cho mỗi cơ sở (tạo mới mỗi lần render sẽ làm iframe tải lại liên tục). */
  private readonly mapUrls = computed(() => {
    const map = new Map<number, SafeResourceUrl>();
    for (const b of this.state.branches()) {
      if (b.mapEmbedUrl && MAP_PATTERN.test(b.mapEmbedUrl)) {
        map.set(b.id, this.sanitizer.bypassSecurityTrustResourceUrl(b.mapEmbedUrl));
      }
    }
    return map;
  });

  mapUrl(b: Branch): SafeResourceUrl | null {
    return this.mapUrls().get(b.id) ?? null;
  }

  select(b: Branch): void {
    this.selectedId.set(b.id);
  }

  tel = telLink;
  wa = (n?: string | null): string | null => whatsappLink(n);
  zalo = zaloLink;

  // ---- form ----
  set(field: 'fullName' | 'phone' | 'email' | 'message', value: string): void {
    this.form.update(f => ({ ...f, [field]: value }));
  }

  nameOk(): boolean {
    return this.form().fullName.trim().length >= 2;
  }
  phoneOk(): boolean {
    const p = this.form().phone.trim();
    return !p || normalizeVnPhone(p) !== null;
  }
  emailOk(): boolean {
    const e = this.form().email.trim();
    return !e || EMAIL_PATTERN.test(e);
  }
  contactOk(): boolean {
    return !!(this.form().phone.trim() || this.form().email.trim());
  }
  messageOk(): boolean {
    return this.form().message.trim().length >= 5;
  }

  send(): void {
    if (!(this.nameOk() && this.phoneOk() && this.emailOk() && this.contactOk() && this.messageOk())) {
      this.showErrors.set(true);
      return;
    }
    const f = this.form();
    this.sending.set(true);
    this.sendError.set(null);
    this.api
      .contact({
        fullName: f.fullName.trim(),
        phone: f.phone.trim() || null,
        email: f.email.trim() || null,
        message: f.message.trim(),
        website: this.website(),
      })
      .subscribe({
        next: () => {
          this.sending.set(false);
          this.sent.set(true);
          this.form.set({ fullName: '', phone: '', email: '', message: '' });
          this.showErrors.set(false);
        },
        error: (err: HttpErrorResponse) => {
          this.sending.set(false);
          this.sendError.set(err.status === 429 ? 'tooMany' : err.status === 0 ? 'network' : 'server');
        },
      });
  }
}
