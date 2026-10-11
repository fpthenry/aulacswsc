import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IContactMessage } from '../contact-message.model';

@Component({
  selector: 'jhi-contact-message-detail',
  templateUrl: './contact-message-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ContactMessageDetailComponent {
  contactMessage = input<IContactMessage | null>(null);

  previousState(): void {
    window.history.back();
  }
}
