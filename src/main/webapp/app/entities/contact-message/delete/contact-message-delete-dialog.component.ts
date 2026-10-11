import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IContactMessage } from '../contact-message.model';
import { ContactMessageService } from '../service/contact-message.service';

@Component({
  templateUrl: './contact-message-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ContactMessageDeleteDialogComponent {
  contactMessage?: IContactMessage;

  protected contactMessageService = inject(ContactMessageService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.contactMessageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
