import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISiteSetting } from '../site-setting.model';
import { SiteSettingService } from '../service/site-setting.service';

@Component({
  templateUrl: './site-setting-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SiteSettingDeleteDialogComponent {
  siteSetting?: ISiteSetting;

  protected siteSettingService = inject(SiteSettingService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.siteSettingService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
