import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ServiceCategoryService } from '../service/service-category.service';
import { IServiceCategory } from '../service-category.model';
import { ServiceCategoryFormGroup, ServiceCategoryFormService } from './service-category-form.service';

@Component({
  selector: 'jhi-service-category-update',
  templateUrl: './service-category-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ServiceCategoryUpdateComponent implements OnInit {
  isSaving = false;
  serviceCategory: IServiceCategory | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected serviceCategoryService = inject(ServiceCategoryService);
  protected serviceCategoryFormService = inject(ServiceCategoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ServiceCategoryFormGroup = this.serviceCategoryFormService.createServiceCategoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ serviceCategory }) => {
      this.serviceCategory = serviceCategory;
      if (serviceCategory) {
        this.updateForm(serviceCategory);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('aulacswscApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const serviceCategory = this.serviceCategoryFormService.getServiceCategory(this.editForm);
    if (serviceCategory.id !== null) {
      this.subscribeToSaveResponse(this.serviceCategoryService.update(serviceCategory));
    } else {
      this.subscribeToSaveResponse(this.serviceCategoryService.create(serviceCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServiceCategory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(serviceCategory: IServiceCategory): void {
    this.serviceCategory = serviceCategory;
    this.serviceCategoryFormService.resetForm(this.editForm, serviceCategory);
  }
}
