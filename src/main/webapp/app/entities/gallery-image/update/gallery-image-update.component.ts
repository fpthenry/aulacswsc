import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { ImageUploadComponent } from 'app/shared/image-upload/image-upload.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBranch } from 'app/entities/branch/branch.model';
import { BranchService } from 'app/entities/branch/service/branch.service';
import { IGalleryImage } from '../gallery-image.model';
import { GalleryImageService } from '../service/gallery-image.service';
import { GalleryImageFormGroup, GalleryImageFormService } from './gallery-image-form.service';

@Component({
  selector: 'jhi-gallery-image-update',
  templateUrl: './gallery-image-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ImageUploadComponent],
})
export class GalleryImageUpdateComponent implements OnInit {
  isSaving = false;
  galleryImage: IGalleryImage | null = null;

  branchesSharedCollection: IBranch[] = [];

  protected galleryImageService = inject(GalleryImageService);
  protected galleryImageFormService = inject(GalleryImageFormService);
  protected branchService = inject(BranchService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GalleryImageFormGroup = this.galleryImageFormService.createGalleryImageFormGroup();

  compareBranch = (o1: IBranch | null, o2: IBranch | null): boolean => this.branchService.compareBranch(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ galleryImage }) => {
      this.galleryImage = galleryImage;
      if (galleryImage) {
        this.updateForm(galleryImage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const galleryImage = this.galleryImageFormService.getGalleryImage(this.editForm);
    if (galleryImage.id !== null) {
      this.subscribeToSaveResponse(this.galleryImageService.update(galleryImage));
    } else {
      this.subscribeToSaveResponse(this.galleryImageService.create(galleryImage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGalleryImage>>): void {
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

  protected updateForm(galleryImage: IGalleryImage): void {
    this.galleryImage = galleryImage;
    this.galleryImageFormService.resetForm(this.editForm, galleryImage);

    this.branchesSharedCollection = this.branchService.addBranchToCollectionIfMissing<IBranch>(
      this.branchesSharedCollection,
      galleryImage.branch,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.branchService
      .query()
      .pipe(map((res: HttpResponse<IBranch[]>) => res.body ?? []))
      .pipe(map((branches: IBranch[]) => this.branchService.addBranchToCollectionIfMissing<IBranch>(branches, this.galleryImage?.branch)))
      .subscribe((branches: IBranch[]) => (this.branchesSharedCollection = branches));
  }
}
