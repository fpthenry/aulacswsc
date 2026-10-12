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
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IBranch } from 'app/entities/branch/branch.model';
import { BranchService } from 'app/entities/branch/service/branch.service';
import { ISpaService } from 'app/entities/spa-service/spa-service.model';
import { SpaServiceService } from 'app/entities/spa-service/service/spa-service.service';
import { StaffService } from '../service/staff.service';
import { IStaff } from '../staff.model';
import { StaffFormGroup, StaffFormService } from './staff-form.service';

@Component({
  selector: 'jhi-staff-update',
  templateUrl: './staff-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ImageUploadComponent, CheckListComponent],
})
export class StaffUpdateComponent implements OnInit {
  isSaving = false;
  staff: IStaff | null = null;

  usersSharedCollection: IUser[] = [];
  branchesSharedCollection: IBranch[] = [];
  spaServicesSharedCollection: ISpaService[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected staffService = inject(StaffService);
  protected staffFormService = inject(StaffFormService);
  protected userService = inject(UserService);
  protected branchService = inject(BranchService);
  protected spaServiceService = inject(SpaServiceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StaffFormGroup = this.staffFormService.createStaffFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareBranch = (o1: IBranch | null, o2: IBranch | null): boolean => this.branchService.compareBranch(o1, o2);

  compareSpaService = (o1: ISpaService | null, o2: ISpaService | null): boolean => this.spaServiceService.compareSpaService(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ staff }) => {
      this.staff = staff;
      if (staff) {
        this.updateForm(staff);
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
    const staff = this.staffFormService.getStaff(this.editForm);
    if (staff.id !== null) {
      this.subscribeToSaveResponse(this.staffService.update(staff));
    } else {
      this.subscribeToSaveResponse(this.staffService.create(staff));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStaff>>): void {
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

  protected updateForm(staff: IStaff): void {
    this.staff = staff;
    this.staffFormService.resetForm(this.editForm, staff);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, staff.user);
    this.branchesSharedCollection = this.branchService.addBranchToCollectionIfMissing<IBranch>(this.branchesSharedCollection, staff.branch);
    this.spaServicesSharedCollection = this.spaServiceService.addSpaServiceToCollectionIfMissing<ISpaService>(
      this.spaServicesSharedCollection,
      ...(staff.services ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.staff?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.branchService
      .query()
      .pipe(map((res: HttpResponse<IBranch[]>) => res.body ?? []))
      .pipe(map((branches: IBranch[]) => this.branchService.addBranchToCollectionIfMissing<IBranch>(branches, this.staff?.branch)))
      .subscribe((branches: IBranch[]) => (this.branchesSharedCollection = branches));

    this.spaServiceService
      .query()
      .pipe(map((res: HttpResponse<ISpaService[]>) => res.body ?? []))
      .pipe(
        map((spaServices: ISpaService[]) =>
          this.spaServiceService.addSpaServiceToCollectionIfMissing<ISpaService>(spaServices, ...(this.staff?.services ?? [])),
        ),
      )
      .subscribe((spaServices: ISpaService[]) => (this.spaServicesSharedCollection = spaServices));
  }
}
