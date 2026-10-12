import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { ImageUploadComponent } from 'app/shared/image-upload/image-upload.component';
import { ImageListUploadComponent } from 'app/shared/image-upload/image-list-upload.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { SiteSettingService } from '../service/site-setting.service';
import { ISiteSetting } from '../site-setting.model';
import { SiteSettingFormGroup, SiteSettingFormService } from './site-setting-form.service';

@Component({
  selector: 'jhi-site-setting-update',
  templateUrl: './site-setting-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ImageUploadComponent, ImageListUploadComponent],
})
export class SiteSettingUpdateComponent implements OnInit {
  isSaving = false;
  siteSetting: ISiteSetting | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected siteSettingService = inject(SiteSettingService);
  protected siteSettingFormService = inject(SiteSettingFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SiteSettingFormGroup = this.siteSettingFormService.createSiteSettingFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ siteSetting }) => {
      this.siteSetting = siteSetting;
      if (siteSetting) {
        this.updateForm(siteSetting);
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
    const siteSetting = this.siteSettingFormService.getSiteSetting(this.editForm);
    if (siteSetting.id !== null) {
      this.subscribeToSaveResponse(this.siteSettingService.update(siteSetting));
    } else {
      this.subscribeToSaveResponse(this.siteSettingService.create(siteSetting));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISiteSetting>>): void {
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

  protected updateForm(siteSetting: ISiteSetting): void {
    this.siteSetting = siteSetting;
    this.siteSettingFormService.resetForm(this.editForm, siteSetting);
  }
}
