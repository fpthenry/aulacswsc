import { DOCUMENT } from '@angular/common';
import { Injectable, inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

import { SITE_LANGS, SiteLang } from './site-lang';

export interface SeoData {
  title: string;
  description?: string | null;
  /** đường dẫn sau tiền tố ngôn ngữ, vd 'services/massage-da-nong' ('' = trang chủ) */
  path: string;
  lang: SiteLang;
  image?: string | null;
  type?: 'website' | 'article';
  /** dữ liệu có cấu trúc schema.org (JSON-LD) */
  jsonLd?: object | object[];
  noindex?: boolean;
}

/**
 * Đặt title, meta description, Open Graph, canonical, hreflang và JSON-LD cho từng trang.
 */
@Injectable({ providedIn: 'root' })
export class SeoService {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly document = inject(DOCUMENT);

  set(data: SeoData): void {
    const origin = this.document.location.origin;
    const url = `${origin}/${data.lang}${data.path ? `/${data.path}` : ''}`;
    const description = (data.description ?? '').slice(0, 300);
    const image = data.image ? new URL(data.image, origin).toString() : null;

    this.title.setTitle(data.title);
    this.setMeta('name', 'description', description);
    this.setMeta('name', 'robots', data.noindex ? 'noindex, nofollow' : 'index, follow');
    this.setMeta('property', 'og:title', data.title);
    this.setMeta('property', 'og:description', description);
    this.setMeta('property', 'og:url', url);
    this.setMeta('property', 'og:type', data.type ?? 'website');
    this.setMeta('property', 'og:locale', data.lang === 'vi' ? 'vi_VN' : 'en_US');
    this.setMeta('property', 'og:image', image);
    this.setMeta('name', 'twitter:card', image ? 'summary_large_image' : 'summary');

    this.setLink('canonical', url);
    for (const lang of SITE_LANGS) {
      this.setLink('alternate', `${origin}/${lang}${data.path ? `/${data.path}` : ''}`, lang);
    }
    this.setLink('alternate', `${origin}/vi${data.path ? `/${data.path}` : ''}`, 'x-default');
    this.setJsonLd(data.jsonLd);
  }

  private setMeta(attr: 'name' | 'property', key: string, content: string | null): void {
    const selector = `${attr}="${key}"`;
    if (content) {
      this.meta.updateTag({ [attr]: key, content }, selector);
    } else {
      this.meta.removeTag(selector);
    }
  }

  private setLink(rel: string, href: string, hreflang?: string): void {
    const selector = hreflang ? `link[rel="${rel}"][hreflang="${hreflang}"]` : `link[rel="${rel}"]:not([hreflang])`;
    let link = this.document.head.querySelector<HTMLLinkElement>(selector);
    if (!link) {
      link = this.document.createElement('link');
      link.rel = rel;
      if (hreflang) {
        link.hreflang = hreflang;
      }
      this.document.head.appendChild(link);
    }
    link.href = href;
  }

  private setJsonLd(data: object | object[] | undefined): void {
    const id = 'site-jsonld';
    this.document.getElementById(id)?.remove();
    if (!data) {
      return;
    }
    const script = this.document.createElement('script');
    script.id = id;
    script.type = 'application/ld+json';
    // JSON.stringify không sinh thẻ </script>; vẫn thay "<" để chắc chắn không chèn được HTML
    script.text = JSON.stringify(data).replace(/</g, '\\u003c');
    this.document.head.appendChild(script);
  }
}
