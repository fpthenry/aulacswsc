import { Component, computed, input, signal } from '@angular/core';
import { AbstractControl } from '@angular/forms';

/**
 * Chọn nhiều bằng ô tích (thay cho <select multiple> phải giữ Ctrl): có ô lọc nhanh, nút Chọn hết / Bỏ hết.
 * Dùng: <jhi-check-list [control]="editForm.controls.staffs" [options]="staffSharedCollection" labelKey="fullName" [compareWith]="compareStaff" />
 */
@Component({
  selector: 'jhi-check-list',
  template: `
    <div class="check-list" [attr.id]="inputId()" role="group">
      @if (options().length > 6) {
        <div class="check-list-tools">
          <input
            type="search"
            class="form-control form-control-sm"
            placeholder="Lọc nhanh…"
            (input)="filter.set($any($event.target).value)"
          />
          <button type="button" class="btn btn-sm btn-secondary" (click)="setAll(true)">Chọn hết</button>
          <button type="button" class="btn btn-sm btn-secondary" (click)="setAll(false)">Bỏ hết</button>
        </div>
      }
      <div class="check-list-items">
        @for (o of visible(); track $index) {
          <label class="check-chip" [class.on]="isChecked(o)">
            <input type="checkbox" [checked]="isChecked(o)" (change)="toggle(o)" />
            <span>{{ label(o) }}</span>
          </label>
        } @empty {
          <span class="form-text">Chưa có dữ liệu.</span>
        }
      </div>
      <div class="form-text">Đã chọn {{ selectedCount() }} / {{ options().length }}</div>
    </div>
  `,
})
export class CheckListComponent<T extends { id: number }> {
  readonly control = input.required<AbstractControl>();
  readonly options = input<T[]>([]);
  readonly labelKey = input<string>('name');
  readonly compareWith = input<(a: T | null, b: T | null) => boolean>((a, b) => a?.id === b?.id);
  readonly inputId = input<string | null>(null);

  readonly filter = signal('');

  readonly visible = computed(() => {
    const q = this.normalize(this.filter());
    return q ? this.options().filter(o => this.normalize(this.label(o)).includes(q)) : this.options();
  });
  readonly selectedCount = computed(() => {
    this.version();
    return this.selected().length;
  });

  private readonly version = signal(0);

  label(o: T): string {
    const v = (o as Record<string, unknown>)[this.labelKey()];
    return typeof v === 'string' && v ? v : `#${o.id}`;
  }

  isChecked(o: T): boolean {
    this.version();
    return this.selected().some(s => this.compareWith()(s, o));
  }

  toggle(o: T): void {
    const cur = this.selected();
    this.write(this.isChecked(o) ? cur.filter(s => !this.compareWith()(s, o)) : [...cur, o]);
  }

  setAll(on: boolean): void {
    const shown = this.visible();
    const rest = this.selected().filter(s => !shown.some(o => this.compareWith()(s, o)));
    this.write(on ? [...rest, ...shown] : rest);
  }

  private selected(): T[] {
    return (this.control().value as T[] | null) ?? [];
  }

  private write(value: T[]): void {
    this.control().setValue(value);
    this.control().markAsDirty();
    this.version.update(v => v + 1);
  }

  private normalize(s: string): string {
    return s
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .toLowerCase()
      .trim();
  }
}
