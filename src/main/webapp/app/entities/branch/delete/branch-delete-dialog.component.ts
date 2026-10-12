import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBranch } from '../branch.model';
import { BranchService } from '../service/branch.service';

@Component({
  templateUrl: './branch-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BranchDeleteDialogComponent {
  branch?: IBranch;

  protected branchService = inject(BranchService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.branchService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
