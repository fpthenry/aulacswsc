import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookingItem } from '../booking-item.model';
import { BookingItemService } from '../service/booking-item.service';

@Component({
  templateUrl: './booking-item-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookingItemDeleteDialogComponent {
  bookingItem?: IBookingItem;

  protected bookingItemService = inject(BookingItemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookingItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
