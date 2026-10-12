import { SiteLang } from './site-lang';
import { BookingConfirmation, Branch, SiteSetting, SpaService } from './site.models';

// ===================== Định dạng =====================

export function formatPrice(value: number | null | undefined, lang: SiteLang): string {
  if (value == null) {
    return '';
  }
  return new Intl.NumberFormat(lang === 'vi' ? 'vi-VN' : 'en-US', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(
    value,
  );
}

export function formatDuration(minutes: number, lang: SiteLang): string {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  if (lang === 'vi') {
    return h ? `${h} giờ${m ? ` ${m} phút` : ''}` : `${m} phút`;
  }
  return h ? `${h}h${m ? ` ${m}m` : ''}` : `${m} min`;
}

export function formatDate(iso: string | null | undefined, lang: SiteLang, withWeekday = false): string {
  if (!iso) {
    return '';
  }
  const date = iso.length === 10 ? new Date(`${iso}T00:00:00`) : new Date(iso);
  return new Intl.DateTimeFormat(lang === 'vi' ? 'vi-VN' : 'en-GB', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    ...(withWeekday ? { weekday: 'long' } : {}),
    timeZone: 'Asia/Ho_Chi_Minh',
  }).format(date);
}

export function formatTime(iso: string): string {
  return new Intl.DateTimeFormat('vi-VN', { hour: '2-digit', minute: '2-digit', hour12: false, timeZone: 'Asia/Ho_Chi_Minh' }).format(
    new Date(iso),
  );
}

/** Ngày hôm nay theo giờ Việt Nam, dạng yyyy-MM-dd. */
export function todayVn(): string {
  return new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Ho_Chi_Minh' }).format(new Date());
}

export function addDays(isoDate: string, days: number): string {
  const d = new Date(`${isoDate}T00:00:00Z`);
  d.setUTCDate(d.getUTCDate() + days);
  return d.toISOString().slice(0, 10);
}

/** Văn bản nhiều dòng → mảng dòng không rỗng (dùng cho "công dụng", "lưu ý"). */
export function lines(text: string | null | undefined): string[] {
  return (text ?? '')
    .split(/\r?\n/)
    .map(l => l.replace(/^[-•*]\s*/, '').trim())
    .filter(Boolean);
}

/** Văn bản → các đoạn (cách nhau bởi dòng trống). Hiển thị dạng text, không chèn HTML. */
export function paragraphs(text: string | null | undefined): string[] {
  return (text ?? '')
    .split(/\r?\n\s*\r?\n/)
    .map(p => p.trim())
    .filter(Boolean);
}

/** So khớp không dấu: "goi dau" khớp "Gội đầu". */
export function normalizeText(text: string): string {
  return text
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase()
    .trim();
}

// ===================== Kiểm tra dữ liệu =====================

/** Số di động Việt Nam: 0xxxxxxxxx hoặc +84xxxxxxxxx, cho phép dấu cách/chấm/gạch. Khớp với kiểm tra ở server. */
export function normalizeVnPhone(raw: string): string | null {
  let digits = raw.replace(/[\s.\-()]/g, '');
  if (digits.startsWith('+84')) {
    digits = `0${digits.slice(3)}`;
  } else if (digits.startsWith('84') && digits.length === 11) {
    digits = `0${digits.slice(2)}`;
  }
  return /^0[35789]\d{8}$/.test(digits) ? digits : null;
}

export const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

// ===================== Ảnh =====================

export interface ImageSet {
  src: string;
  srcset?: string;
  placeholder?: string;
}

/**
 * Ảnh upload qua trang quản trị được lưu 3 cỡ: abc.webp (1600px), abc-800.webp, abc-24.webp (nền mờ).
 * Ảnh dán link ngoài thì chỉ có 1 cỡ.
 */
export function imageSet(url: string | null | undefined): ImageSet | null {
  if (!url) {
    return null;
  }
  const m = /^(\/uploads\/.+)\.webp$/.exec(url);
  if (!m) {
    return { src: url };
  }
  return { src: url, srcset: `${m[1]}-800.webp 800w, ${url} 1600w`, placeholder: `${m[1]}-24.webp` };
}

// ===================== Liên hệ =====================

export function telLink(phone: string | null | undefined): string | null {
  return phone ? `tel:${phone.replace(/[^\d+]/g, '')}` : null;
}

export function whatsappLink(number: string | null | undefined, text?: string): string | null {
  if (!number) {
    return null;
  }
  let digits = number.replace(/\D/g, '');
  if (digits.startsWith('0')) {
    digits = `84${digits.slice(1)}`;
  }
  return `https://wa.me/${digits}${text ? `?text=${encodeURIComponent(text)}` : ''}`;
}

export function zaloLink(number: string | null | undefined): string | null {
  return number ? `https://zalo.me/${number.replace(/\D/g, '')}` : null;
}

/** Link YouTube → id video (để nhúng youtube-nocookie). */
export function youtubeId(url: string | null | undefined): string | null {
  if (!url) {
    return null;
  }
  const m = /(?:youtube\.com\/(?:watch\?v=|shorts\/|embed\/)|youtu\.be\/)([\w-]{11})/.exec(url);
  return m ? m[1] : null;
}

// ===================== Lịch (.ics) =====================

/** Tạo file .ics cho nút "Thêm vào lịch". */
export function bookingIcs(b: BookingConfirmation, lang: SiteLang, siteName: string): string {
  const stamp = (iso: string): string =>
    new Date(iso)
      .toISOString()
      .replace(/[-:]/g, '')
      .replace(/\.\d{3}/, '');
  const esc = (s: string): string => s.replace(/\\/g, '\\\\').replace(/;/g, '\\;').replace(/,/g, '\\,').replace(/\n/g, '\\n');
  const services = b.services.map(s => (lang === 'vi' ? s.nameVi : s.nameEn || s.nameVi)).join(', ');
  const address = lang === 'en' && b.branch.addressEn ? b.branch.addressEn : b.branch.addressVi;
  const summary = `${siteName} – ${services}`;
  const description = `${lang === 'vi' ? 'Mã đặt lịch' : 'Booking code'}: ${b.code}\n${services}\n${b.branch.phone}`;
  return [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//aulacswsc//booking//VI',
    'CALSCALE:GREGORIAN',
    'METHOD:PUBLISH',
    'BEGIN:VEVENT',
    `UID:${b.code}@aulacswsc`,
    `DTSTAMP:${stamp(new Date().toISOString())}`,
    `DTSTART:${stamp(b.startTime)}`,
    `DTEND:${stamp(b.endTime)}`,
    `SUMMARY:${esc(summary)}`,
    `LOCATION:${esc(address)}`,
    `DESCRIPTION:${esc(description)}`,
    'BEGIN:VALARM',
    'TRIGGER:-PT2H',
    'ACTION:DISPLAY',
    `DESCRIPTION:${esc(summary)}`,
    'END:VALARM',
    'END:VEVENT',
    'END:VCALENDAR',
  ].join('\r\n');
}

export function downloadFile(content: string, fileName: string, mime: string): void {
  const url = URL.createObjectURL(new Blob([content], { type: mime }));
  const a = document.createElement('a');
  a.href = url;
  a.download = fileName;
  document.body.appendChild(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// ===================== schema.org =====================

export function localBusinessLd(setting: SiteSetting, branches: Branch[], origin: string, lang: SiteLang): object[] {
  return branches.map(b => ({
    '@context': 'https://schema.org',
    '@type': 'DaySpa',
    name: lang === 'vi' ? b.nameVi : b.nameEn,
    url: `${origin}/${lang}`,
    telephone: b.phone,
    email: b.email ?? setting.email ?? undefined,
    image: b.imageUrl ? new URL(b.imageUrl, origin).toString() : undefined,
    priceRange: '₫₫',
    address: {
      '@type': 'PostalAddress',
      streetAddress: lang === 'en' && b.addressEn ? b.addressEn : b.addressVi,
      addressCountry: 'VN',
    },
    openingHoursSpecification: [
      {
        '@type': 'OpeningHoursSpecification',
        dayOfWeek: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
        opens: b.openTime,
        closes: b.closeTime,
      },
    ],
    sameAs: [setting.facebookUrl, setting.instagramUrl, setting.tiktokUrl, setting.youtubeUrl].filter(Boolean),
  }));
}

export function serviceLd(service: SpaService, providerName: string, origin: string, lang: SiteLang): object {
  const name = lang === 'vi' ? service.nameVi : service.nameEn || service.nameVi;
  return {
    '@context': 'https://schema.org',
    '@type': 'Service',
    name,
    description: (lang === 'vi' ? service.shortDescriptionVi : service.shortDescriptionEn) ?? undefined,
    image: service.imageUrl ? new URL(service.imageUrl, origin).toString() : undefined,
    provider: { '@type': 'DaySpa', name: providerName },
    offers: {
      '@type': 'Offer',
      price: service.salePrice ?? service.price,
      priceCurrency: 'VND',
      availability: 'https://schema.org/InStock',
      url: `${origin}/${lang}/services/${service.slug}`,
    },
  };
}
