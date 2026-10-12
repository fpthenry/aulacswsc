import { Directive, ElementRef, OnDestroy, OnInit, inject, input } from '@angular/core';

/** Kiểu xuất hiện: '' = trồi lên (mặc định), left/right = trượt ngang, zoom = phóng ra, curtain = vén rèm, head = tiêu đề mục */
export type RevealVariant = '' | 'left' | 'right' | 'zoom' | 'curtain' | 'head';

/**
 * Hiệu ứng xuất hiện khi phần tử vào khung nhìn (một IntersectionObserver chung, không lắng nghe scroll).
 * Người dùng bật "giảm chuyển động" → hiện ngay, không animation (xử lý trong CSS .reveal).
 *
 *   <section siteReveal>…</section>
 *   <div siteReveal="zoom" [revealDelay]="100">…</div>
 *   <div class="section-head" siteReveal="head">…</div>
 */
@Directive({
  selector: '[siteReveal]',
  host: { class: 'reveal' },
})
export class RevealDirective implements OnInit, OnDestroy {
  /** kiểu xuất hiện (RevealVariant); nhận string để dùng được biểu thức trong template */
  readonly siteReveal = input<string>('');
  readonly revealDelay = input(0);

  private static observer?: IntersectionObserver;
  private readonly el = inject<ElementRef<HTMLElement>>(ElementRef);

  ngOnInit(): void {
    const node = this.el.nativeElement;
    if (this.siteReveal()) {
      node.classList.add(`reveal--${this.siteReveal()}`);
    }
    if (this.revealDelay()) {
      node.style.transitionDelay = `${this.revealDelay()}ms`;
      node.style.setProperty('--reveal-delay', `${this.revealDelay()}ms`);
    }
    if (typeof IntersectionObserver === 'undefined') {
      node.classList.add('is-visible');
      return;
    }
    RevealDirective.observer ??= new IntersectionObserver(
      entries => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            RevealDirective.observer?.unobserve(entry.target);
          }
        }
      },
      { rootMargin: '0px 0px -8% 0px', threshold: 0.08 },
    );
    RevealDirective.observer.observe(node);
  }

  ngOnDestroy(): void {
    RevealDirective.observer?.unobserve(this.el.nativeElement);
  }
}
