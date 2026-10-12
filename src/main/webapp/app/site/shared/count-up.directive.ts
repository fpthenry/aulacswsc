import { Directive, ElementRef, OnDestroy, effect, inject, input } from '@angular/core';

/**
 * Số đếm tăng dần (0 → giá trị) khi phần tử vào khung nhìn. Giảm chuyển động → hiện ngay số cuối.
 *
 *   <strong [siteCountUp]="18"></strong>
 *   <strong [siteCountUp]="4.9" [decimals]="1"></strong>
 */
@Directive({
  selector: '[siteCountUp]',
})
export class CountUpDirective implements OnDestroy {
  readonly siteCountUp = input.required<number>();
  readonly decimals = input(0);
  readonly duration = input(1600);

  private readonly el = inject<ElementRef<HTMLElement>>(ElementRef);
  private observer?: IntersectionObserver;
  private frame = 0;
  private started = false;

  constructor() {
    effect(() => {
      const target = this.siteCountUp();
      const node = this.el.nativeElement;
      const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      if (reduced || typeof IntersectionObserver === 'undefined') {
        node.textContent = this.format(target);
        return;
      }
      if (this.started) {
        // dữ liệu cập nhật sau khi đã chạy xong → hiện luôn số mới
        node.textContent = this.format(target);
        return;
      }
      node.textContent = this.format(0);
      this.observer?.disconnect();
      this.observer = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting) {
            this.observer?.disconnect();
            this.run(target);
          }
        },
        { threshold: 0.4 },
      );
      this.observer.observe(node);
    });
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
    cancelAnimationFrame(this.frame);
  }

  private run(target: number): void {
    this.started = true;
    const start = performance.now();
    const step = (now: number): void => {
      const t = Math.min(1, (now - start) / this.duration());
      // ease-out cubic: chạy nhanh rồi chậm dần ở cuối
      const eased = 1 - (1 - t) ** 3;
      this.el.nativeElement.textContent = this.format(target * eased);
      if (t < 1) {
        this.frame = requestAnimationFrame(step);
      }
    };
    this.frame = requestAnimationFrame(step);
  }

  private format(value: number): string {
    return value.toLocaleString(document.documentElement.lang === 'en' ? 'en-US' : 'vi-VN', {
      minimumFractionDigits: this.decimals(),
      maximumFractionDigits: this.decimals(),
    });
  }
}
