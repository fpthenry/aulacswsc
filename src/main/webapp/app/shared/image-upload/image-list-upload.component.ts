import { Component, DestroyRef, OnInit, computed, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl } from '@angular/forms';

import { ImageUploadComponent } from './image-upload.component';

/**
 * Quản lý DANH SÁCH ảnh lưu trong một ô văn bản (mỗi dòng một URL) — dùng cho Hero Slides.
 * Xem ảnh thu nhỏ, thêm (upload/dán link), xóa, đổi thứ tự.
 *
 *   <jhi-image-list-upload [control]="editForm.controls.heroSlides" />
 */
@Component({
  selector: 'jhi-image-list-upload',
  imports: [ImageUploadComponent],
  template: `
    <div class="list">
      @for (url of urls(); track url + $index; let i = $index; let last = $last) {
        <figure class="item">
          <img [src]="url" alt="" />
          <figcaption>
            <span class="badge bg-secondary">{{ i + 1 }}</span>
            <button type="button" class="btn btn-sm btn-light" (click)="move(i, -1)" [disabled]="i === 0" title="Lên trước">←</button>
            <button type="button" class="btn btn-sm btn-light" (click)="move(i, 1)" [disabled]="last" title="Ra sau">→</button>
            <button type="button" class="btn btn-sm btn-outline-danger" (click)="remove(i)" title="Xóa ảnh">✕</button>
          </figcaption>
        </figure>
      } @empty {
        <p class="text-muted small mb-0">Chưa có ảnh slideshow — website sẽ dùng "Hero Image Url".</p>
      }
    </div>
    <div class="add">
      <small class="text-muted d-block mb-1">Thêm ảnh (ưu tiên ảnh ngang, tông tối, không có chữ):</small>
      <jhi-image-upload [control]="pending" />
    </div>
  `,
  styles: `
    .list {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
      margin-bottom: 0.75rem;
    }
    .item {
      width: 180px;
      margin: 0;
      border: 1px solid #ddd;
      border-radius: 8px;
      overflow: hidden;
      background: #f7f7f7;
    }
    .item img {
      display: block;
      width: 100%;
      height: 100px;
      object-fit: cover;
    }
    figcaption {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      padding: 0.35rem;
    }
    figcaption .badge {
      margin-right: auto;
    }
  `,
})
export class ImageListUploadComponent implements OnInit {
  readonly control = input.required<FormControl<string | null | undefined>>();

  /** ô tạm để ImageUploadComponent ghi URL vừa upload */
  readonly pending = new FormControl<string | null | undefined>(null);
  readonly urls = computed(() =>
    this.value()
      .split(/\r?\n/)
      .map(u => u.trim())
      .filter(Boolean),
  );
  /** nội dung ô văn bản (mỗi dòng 1 URL) */
  private readonly value = signal<string>('');
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    const control = this.control();
    this.value.set(control.value ?? '');
    control.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(v => this.value.set(v ?? ''));
    this.pending.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(url => {
      if (url) {
        this.write([...this.urls(), url]);
        this.pending.setValue(null, { emitEvent: false });
      }
    });
  }

  remove(i: number): void {
    this.write(this.urls().filter((_, k) => k !== i));
  }

  move(i: number, delta: number): void {
    const list = [...this.urls()];
    const j = i + delta;
    [list[i], list[j]] = [list[j], list[i]];
    this.write(list);
  }

  private write(list: string[]): void {
    const control = this.control();
    control.setValue(list.length ? list.join('\n') : null);
    control.markAsDirty();
  }
}
