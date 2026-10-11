import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPromotion } from '../promotion.model';

@Component({
  selector: 'jhi-promotion-detail',
  templateUrl: './promotion-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PromotionDetailComponent {
  promotion = input<IPromotion | null>(null);

  previousState(): void {
    window.history.back();
  }
}
