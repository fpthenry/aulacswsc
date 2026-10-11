import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IBookingItem } from '../booking-item.model';

@Component({
  selector: 'jhi-booking-item-detail',
  templateUrl: './booking-item-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class BookingItemDetailComponent {
  bookingItem = input<IBookingItem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
