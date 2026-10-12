import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { CheckListComponent } from 'app/backoffice/shared/check-list.component';

import SharedModule from 'app/shared/shared.module';
import { ImageUploadComponent } from 'app/shared/image-upload/image-upload.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IServiceCategory } from 'app/entities/service-category/service-category.model';
import { ServiceCategoryService } from 'app/entities/service-category/service/service-category.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { SpaServiceService } from '../service/spa-service.service';
import { ISpaService } from '../spa-service.model';
import { SpaServiceFormGroup, SpaServiceFormService } from './spa-service-form.service';

@Component({
  selector: 'jhi-spa-service-update',
  templateUrl: './spa-service-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ImageUploadComponent, CheckListComponent],
})
export class SpaServiceUpdateComponent implements OnInit {
  isSaving = false;
  spaService: ISpaService | null = null;

  serviceCategoriesSharedCollection: IServiceCategory[] = [];
  staffSharedCollection: IStaff[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected spaServiceService = inject(SpaServiceService);
  protected spaServiceFormService = inject(SpaServiceFormService);
  protected serviceCategoryService = inject(ServiceCategoryService);
  protected staffService = inject(StaffService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SpaServiceFormGroup = this.spaServiceFormService.createSpaServiceFormGroup();

  compareServiceCategory = (o1: IServiceCategory | null, o2: IServiceCategory | null): boolean =>
    this.serviceCategoryService.compareServiceCategory(o1, o2);

  compareStaff = (o1: IStaff | null, o2: IStaff | null): boolean => this.staffService.compareStaff(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spaService }) => {
      this.spaService = spaService;
      if (spaService) {
        this.updateForm(spaService);
      }

      this.loadRelationshipsOptions();
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
    const spaService = this.spaServiceFormService.getSpaService(this.editForm);
    if (spaService.id !== null) {
      this.subscribeToSaveResponse(this.spaServiceService.update(spaService));
    } else {
      this.subscribeToSaveResponse(this.spaServiceService.create(spaService));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpaService>>): void {
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

  protected updateForm(spaService: ISpaService): void {
    this.spaService = spaService;
    this.spaServiceFormService.resetForm(this.editForm, spaService);

    this.serviceCategoriesSharedCollection = this.serviceCategoryService.addServiceCategoryToCollectionIfMissing<IServiceCategory>(
      this.serviceCategoriesSharedCollection,
      spaService.category,
    );
    this.staffSharedCollection = this.staffService.addStaffToCollectionIfMissing<IStaff>(
      this.staffSharedCollection,
      ...(spaService.staffs ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.serviceCategoryService
      .query()
      .pipe(map((res: HttpResponse<IServiceCategory[]>) => res.body ?? []))
      .pipe(
        map((serviceCategories: IServiceCategory[]) =>
          this.serviceCategoryService.addServiceCategoryToCollectionIfMissing<IServiceCategory>(
            serviceCategories,
            this.spaService?.category,
          ),
        ),
      )
      .subscribe((serviceCategories: IServiceCategory[]) => (this.serviceCategoriesSharedCollection = serviceCategories));

    this.staffService
      .query()
      .pipe(map((res: HttpResponse<IStaff[]>) => res.body ?? []))
      .pipe(map((staff: IStaff[]) => this.staffService.addStaffToCollectionIfMissing<IStaff>(staff, ...(this.spaService?.staffs ?? []))))
      .subscribe((staff: IStaff[]) => (this.staffSharedCollection = staff));
  }
}
