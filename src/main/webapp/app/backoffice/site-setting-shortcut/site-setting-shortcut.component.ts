import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { SiteSettingService } from 'app/entities/site-setting/service/site-setting.service';

/**
 * Lối tắt "Cài đặt website": website chỉ dùng bản ghi đầu tiên → mở thẳng form sửa bản ghi đó
 * (chưa có thì mở form tạo mới), không bắt người dùng chọn trong danh sách.
 */
@Component({
  selector: 'jhi-site-setting-shortcut',
  template: '<p class="ad-muted">Đang mở cài đặt website…</p>',
})
export default class SiteSettingShortcutComponent implements OnInit {
  private readonly service = inject(SiteSettingService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.service.query({ sort: ['id,asc'], size: 1 }).subscribe({
      next: res => {
        const first = res.body?.[0];
        void this.router.navigate(first ? ['/site-setting', first.id, 'edit'] : ['/site-setting', 'new'], { replaceUrl: true });
      },
      error: () => void this.router.navigate(['/site-setting'], { replaceUrl: true }),
    });
  }
}
