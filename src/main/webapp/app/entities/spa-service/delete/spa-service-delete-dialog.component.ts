import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISpaService } from '../spa-service.model';
import { SpaServiceService } from '../service/spa-service.service';

@Component({
  templateUrl: './spa-service-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SpaServiceDeleteDialogComponent {
  spaService?: ISpaService;

  protected spaServiceService = inject(SpaServiceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spaServiceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
