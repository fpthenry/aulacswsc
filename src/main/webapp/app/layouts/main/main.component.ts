import { Component, OnInit, Renderer2, RendererFactory2, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, finalize } from 'rxjs/operators';
import { isSiteUrl } from 'app/site/core/site-lang';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';

import { AccountService } from 'app/core/auth/account.service';
import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { AdminShellComponent } from 'app/backoffice/layout/admin-shell.component';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, AdminShellComponent],
})
export default class MainComponent implements OnInit {
  /** true khi đang ở website khách /vi, /en → ẩn navbar/footer quản trị */
  readonly isSite = signal(isSiteUrl(window.location.pathname));
  /** đã kiểm tra xong trạng thái đăng nhập (tránh nháy layout lúc mở trang) */
  readonly authChecked = signal(false);
  readonly account = inject(AccountService).trackCurrentAccount();

  private readonly renderer: Renderer2;

  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly translateService = inject(TranslateService);
  private readonly rootRenderer = inject(RendererFactory2);

  constructor() {
    this.renderer = this.rootRenderer.createRenderer(document.querySelector('html'), null);
  }

  ngOnInit(): void {
    this.router.events.pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd)).subscribe(e => {
      this.isSite.set(isSiteUrl(e.urlAfterRedirects));
    });

    // try to log in automatically
    this.accountService
      .identity()
      .pipe(finalize(() => this.authChecked.set(true)))
      .subscribe({ next: () => this.authChecked.set(true), error: () => this.authChecked.set(true) });

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(document.querySelector('html'), 'lang', langChangeEvent.lang);
    });
  }
}
