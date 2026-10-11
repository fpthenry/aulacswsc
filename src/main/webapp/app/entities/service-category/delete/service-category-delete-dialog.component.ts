import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IServiceCategory } from '../service-category.model';
import { ServiceCategoryService } from '../service/service-category.service';

@Component({
  templateUrl: './service-category-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ServiceCategoryDeleteDialogComponent {
  serviceCategory?: IServiceCategory;

  protected serviceCategoryService = inject(ServiceCategoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.serviceCategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
