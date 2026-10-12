import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IServiceCategory } from '../service-category.model';

@Component({
  selector: 'jhi-service-category-detail',
  templateUrl: './service-category-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ServiceCategoryDetailComponent {
  serviceCategory = input<IServiceCategory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
