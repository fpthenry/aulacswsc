import { CanMatchFn, UrlSegment } from '@angular/router';

/** Ngôn ngữ của website khách; đường dẫn luôn bắt đầu bằng /vi hoặc /en. */
export type SiteLang = 'vi' | 'en';
export const SITE_LANGS: SiteLang[] = ['vi', 'en'];
export const DEFAULT_SITE_LANG: SiteLang = 'vi';
const LANG_KEY = 'site.lang';

export const isSiteLang = (value: unknown): value is SiteLang => value === 'vi' || value === 'en';

/** Chỉ cho route ':lang' khớp khi đoạn đầu là vi/en (không nuốt /admin, /login...). */
export const siteLangMatch: CanMatchFn = (_route, segments: UrlSegment[]) => isSiteLang(segments[0]?.path);

export const isSiteUrl = (url: string): boolean => /^\/(vi|en)(\/|\?|#|$)/.test(url);

export function preferredSiteLang(): SiteLang {
  try {
    const saved = localStorage.getItem(LANG_KEY);
    if (isSiteLang(saved)) {
      return saved;
    }
  } catch {
    // localStorage bị chặn → dùng mặc định
  }
  const browser = navigator.language.toLowerCase();
  return browser === '' || browser.startsWith('vi') ? DEFAULT_SITE_LANG : 'en';
}

export function rememberSiteLang(lang: SiteLang): void {
  try {
    localStorage.setItem(LANG_KEY, lang);
  } catch {
    // bỏ qua
  }
}

/** Lấy trường theo ngôn ngữ: pick(service, 'name', 'en') → service.nameEn, rỗng thì dùng nameVi. */
export function pick(obj: object | null | undefined, field: string, lang: SiteLang): string {
  if (!obj) {
    return '';
  }
  const record = obj as Record<string, unknown>;
  const vi = record[`${field}Vi`];
  const en = record[`${field}En`];
  // chuỗi rỗng cũng coi như chưa nhập → dùng ngôn ngữ còn lại
  const text = (v: unknown): string | null => (typeof v === 'string' && v.trim() !== '' ? v : null);
  return (lang === 'en' ? (text(en) ?? text(vi)) : (text(vi) ?? text(en))) ?? '';
}
