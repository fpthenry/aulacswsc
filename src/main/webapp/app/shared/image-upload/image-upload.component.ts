import { HttpClient, HttpEventType } from '@angular/common/http';
import { Component, inject, input, output, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface UploadResult {
  url: string;
  mediumUrl?: string;
  placeholderUrl?: string;
}

/** Cỡ ảnh tạo ra trước khi upload (khớp với UploadResource và imageSet() phía website). */
const SIZES = [
  { field: 'image', max: 1600, quality: 0.82 },
  { field: 'medium', max: 800, quality: 0.8 },
  { field: 'placeholder', max: 24, quality: 0.5 },
] as const;
const MAX_INPUT_BYTES = 20 * 1024 * 1024;

/**
 * Ô chọn ảnh cho form quản trị: xem trước, chọn file, nén sang WebP ngay trên trình duyệt
 * (1600px + 800px + 24px làm nền mờ), upload lên /api/admin/uploads rồi ghi URL vào form control.
 * Vẫn cho phép dán link ảnh ngoài.
 *
 *   <jhi-image-upload [control]="editForm.controls.imageUrl" inputId="field_imageUrl" (dimensions)="..." />
 */
@Component({
  selector: 'jhi-image-upload',
  imports: [ReactiveFormsModule],
  template: `
    <div class="image-upload">
      <div class="preview">
        @if (control().value) {
          <img [src]="control().value" alt="" />
        } @else {
          <span class="text-muted small">Chưa có ảnh</span>
        }
      </div>
      <div class="controls">
        <div class="d-flex flex-wrap gap-2 align-items-center">
          <label class="btn btn-outline-primary btn-sm mb-0" [class.disabled]="busy()">
            {{ busy() ? 'Đang tải lên… ' + progress() + '%' : 'Chọn ảnh từ máy' }}
            <input type="file" accept="image/*" class="d-none" (change)="onFile($event)" [disabled]="busy()" />
          </label>
          @if (control().value) {
            <button type="button" class="btn btn-outline-secondary btn-sm" (click)="clear()" [disabled]="busy()">Xóa ảnh</button>
          }
        </div>
        <input
          type="text"
          class="form-control form-control-sm mt-2"
          [id]="inputId()"
          [formControl]="control()"
          placeholder="/uploads/... hoặc https://..."
        />
        <small class="form-text text-muted">Ảnh tự nén sang WebP (tối đa 1600px). Nên dùng ảnh ngang, tối thiểu 1200px.</small>
        @if (error()) {
          <div class="text-danger small mt-1" role="alert">{{ error() }}</div>
        }
      </div>
    </div>
  `,
  styles: `
    .image-upload {
      display: flex;
      flex-wrap: wrap;
      gap: 1rem;
      align-items: flex-start;
    }
    .preview {
      display: grid;
      place-items: center;
      width: 160px;
      height: 110px;
      border: 1px dashed #ccc;
      border-radius: 8px;
      overflow: hidden;
      background: #f7f7f7;
    }
    .preview img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .controls {
      flex: 1;
      min-width: 240px;
    }
  `,
})
export class ImageUploadComponent {
  readonly control = input.required<FormControl<string | null | undefined>>();
  readonly inputId = input<string>();
  /** kích thước ảnh gốc sau khi resize (dùng cho GalleryImage.width/height) */
  readonly dimensions = output<{ width: number; height: number }>();

  readonly busy = signal(false);
  readonly progress = signal(0);
  readonly error = signal<string | null>(null);

  private readonly http = inject(HttpClient);
  private readonly config = inject(ApplicationConfigService);

  async onFile(event: Event): Promise<void> {
    const inputEl = event.target as HTMLInputElement;
    const file = inputEl.files?.[0];
    inputEl.value = '';
    if (!file) {
      return;
    }
    this.error.set(null);
    if (!file.type.startsWith('image/')) {
      this.error.set('File không phải ảnh.');
      return;
    }
    if (file.size > MAX_INPUT_BYTES) {
      this.error.set('Ảnh quá lớn (tối đa 20MB).');
      return;
    }
    this.busy.set(true);
    this.progress.set(0);
    try {
      const bitmap = await createImageBitmap(file);
      const form = new FormData();
      let mainSize = { width: bitmap.width, height: bitmap.height };
      for (const size of SIZES) {
        const { blob, width, height } = await resize(bitmap, size.max, size.quality);
        if (size.field === 'image') {
          mainSize = { width, height };
        }
        form.append(size.field, blob, `${size.field}.webp`);
      }
      bitmap.close();
      this.http
        .post<UploadResult>(this.config.getEndpointFor('api/admin/uploads'), form, { reportProgress: true, observe: 'events' })
        .subscribe({
          next: e => {
            if (e.type === HttpEventType.UploadProgress && e.total) {
              this.progress.set(Math.round((e.loaded / e.total) * 100));
            } else if (e.type === HttpEventType.Response && e.body) {
              this.control().setValue(e.body.url);
              this.control().markAsDirty();
              this.dimensions.emit(mainSize);
              this.busy.set(false);
            }
          },
          error: () => {
            this.error.set('Upload thất bại. Kiểm tra quyền ADMIN và dung lượng ảnh.');
            this.busy.set(false);
          },
        });
    } catch {
      this.error.set('Không đọc được ảnh này. Hãy thử ảnh JPG/PNG khác.');
      this.busy.set(false);
    }
  }

  clear(): void {
    this.control().setValue(null);
    this.control().markAsDirty();
  }
}

/** Thu nhỏ ảnh về cạnh dài tối đa `max` px và mã hóa WebP (trình duyệt không hỗ trợ WebP sẽ trả PNG/JPEG — server vẫn nhận). */
async function resize(bitmap: ImageBitmap, max: number, quality: number): Promise<{ blob: Blob; width: number; height: number }> {
  const scale = Math.min(1, max / Math.max(bitmap.width, bitmap.height));
  const width = Math.max(1, Math.round(bitmap.width * scale));
  const height = Math.max(1, Math.round(bitmap.height * scale));
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext('2d');
  if (!ctx) {
    throw new Error('Canvas not supported');
  }
  ctx.imageSmoothingQuality = 'high';
  ctx.drawImage(bitmap, 0, 0, width, height);
  const blob = await new Promise<Blob | null>(resolve => canvas.toBlob(resolve, 'image/webp', quality));
  if (!blob) {
    throw new Error('Encode failed');
  }
  return { blob, width, height };
}
