import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISpaService } from 'app/entities/spa-service/spa-service.model';
import { SpaServiceService } from 'app/entities/spa-service/service/spa-service.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { IBooking } from 'app/entities/booking/booking.model';
import { BookingService } from 'app/entities/booking/service/booking.service';
import { BookingItemService } from '../service/booking-item.service';
import { IBookingItem } from '../booking-item.model';
import { BookingItemFormGroup, BookingItemFormService } from './booking-item-form.service';

@Component({
  selector: 'jhi-booking-item-update',
  templateUrl: './booking-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookingItemUpdateComponent implements OnInit {
  isSaving = false;
  bookingItem: IBookingItem | null = null;

  spaServicesSharedCollection: ISpaService[] = [];
  staffSharedCollection: IStaff[] = [];
  bookingsSharedCollection: IBooking[] = [];

  protected bookingItemService = inject(BookingItemService);
  protected bookingItemFormService = inject(BookingItemFormService);
  protected spaServiceService = inject(SpaServiceService);
  protected staffService = inject(StaffService);
  protected bookingService = inject(BookingService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookingItemFormGroup = this.bookingItemFormService.createBookingItemFormGroup();

  compareSpaService = (o1: ISpaService | null, o2: ISpaService | null): boolean => this.spaServiceService.compareSpaService(o1, o2);

  compareStaff = (o1: IStaff | null, o2: IStaff | null): boolean => this.staffService.compareStaff(o1, o2);

  compareBooking = (o1: IBooking | null, o2: IBooking | null): boolean => this.bookingService.compareBooking(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookingItem }) => {
      this.bookingItem = bookingItem;
      if (bookingItem) {
        this.updateForm(bookingItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookingItem = this.bookingItemFormService.getBookingItem(this.editForm);
    if (bookingItem.id !== null) {
      this.subscribeToSaveResponse(this.bookingItemService.update(bookingItem));
    } else {
      this.subscribeToSaveResponse(this.bookingItemService.create(bookingItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookingItem>>): void {
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

  protected updateForm(bookingItem: IBookingItem): void {
    this.bookingItem = bookingItem;
    this.bookingItemFormService.resetForm(this.editForm, bookingItem);

    this.spaServicesSharedCollection = this.spaServiceService.addSpaServiceToCollectionIfMissing<ISpaService>(
      this.spaServicesSharedCollection,
      bookingItem.service,
    );
    this.staffSharedCollection = this.staffService.addStaffToCollectionIfMissing<IStaff>(this.staffSharedCollection, bookingItem.staff);
    this.bookingsSharedCollection = this.bookingService.addBookingToCollectionIfMissing<IBooking>(
      this.bookingsSharedCollection,
      bookingItem.booking,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.spaServiceService
      .query()
      .pipe(map((res: HttpResponse<ISpaService[]>) => res.body ?? []))
      .pipe(
        map((spaServices: ISpaService[]) =>
          this.spaServiceService.addSpaServiceToCollectionIfMissing<ISpaService>(spaServices, this.bookingItem?.service),
        ),
      )
      .subscribe((spaServices: ISpaService[]) => (this.spaServicesSharedCollection = spaServices));

    this.staffService
      .query()
      .pipe(map((res: HttpResponse<IStaff[]>) => res.body ?? []))
      .pipe(map((staff: IStaff[]) => this.staffService.addStaffToCollectionIfMissing<IStaff>(staff, this.bookingItem?.staff)))
      .subscribe((staff: IStaff[]) => (this.staffSharedCollection = staff));

    this.bookingService
      .query()
      .pipe(map((res: HttpResponse<IBooking[]>) => res.body ?? []))
      .pipe(
        map((bookings: IBooking[]) => this.bookingService.addBookingToCollectionIfMissing<IBooking>(bookings, this.bookingItem?.booking)),
      )
      .subscribe((bookings: IBooking[]) => (this.bookingsSharedCollection = bookings));
  }
}
