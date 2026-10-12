import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, shareReplay } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {
  BlogCategory,
  BlogPost,
  BookingConfirmation,
  BookingRequest,
  GalleryImage,
  Paged,
  Review,
  SiteBootstrap,
  TimeSlot,
} from './site.models';

/** Gọi các API công khai /api/public/** (không cần đăng nhập). */
@Injectable({ providedIn: 'root' })
export class SiteApiService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ApplicationConfigService);

  private bootstrap$?: Observable<SiteBootstrap>;

  /** Cài đặt + cơ sở + danh mục + dịch vụ: tải 1 lần, dùng chung cho mọi trang. */
  site(): Observable<SiteBootstrap> {
    this.bootstrap$ ??= this.http.get<SiteBootstrap>(this.url('site')).pipe(shareReplay(1));
    return this.bootstrap$;
  }

  gallery(): Observable<GalleryImage[]> {
    return this.http.get<GalleryImage[]>(this.url('gallery'));
  }

  reviews(page = 0, size = 12): Observable<Paged<Review>> {
    return this.paged<Review>('reviews', new HttpParams().set('page', page).set('size', size));
  }

  blog(page = 0, size = 9, category?: BlogCategory | null): Observable<Paged<BlogPost>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (category) {
      params = params.set('category', category);
    }
    return this.paged<BlogPost>('blog', params);
  }

  blogPost(slug: string): Observable<BlogPost> {
    return this.http.get<BlogPost>(this.url(`blog/${encodeURIComponent(slug)}`));
  }

  availability(branchId: number, date: string, duration: number, people: number): Observable<TimeSlot[]> {
    const params = new HttpParams().set('branchId', branchId).set('date', date).set('duration', duration).set('people', people);
    return this.http.get<TimeSlot[]>(this.url('bookings/availability'), { params });
  }

  book(request: BookingRequest): Observable<BookingConfirmation> {
    return this.http.post<BookingConfirmation>(this.url('bookings'), request);
  }

  lookup(code: string, phone: string): Observable<BookingConfirmation> {
    return this.http.post<BookingConfirmation>(this.url('bookings/lookup'), { code, phone });
  }

  contact(body: { fullName: string; phone: string | null; email: string | null; message: string; website: string }): Observable<unknown> {
    return this.http.post(this.url('contact'), body);
  }

  private paged<T>(path: string, params: HttpParams): Observable<Paged<T>> {
    return this.http
      .get<T[]>(this.url(path), { params, observe: 'response' })
      .pipe(map((res: HttpResponse<T[]>) => ({ items: res.body ?? [], total: Number(res.headers.get('X-Total-Count') ?? 0) })));
  }

  private url(path: string): string {
    return this.config.getEndpointFor(`api/public/${path}`);
  }
}
