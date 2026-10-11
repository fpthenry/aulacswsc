import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPromotion } from 'app/entities/promotion/promotion.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { BookingStatus } from 'app/entities/enumerations/booking-status.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';
import { BookingService } from '../service/booking.service';
import { IBooking } from '../booking.model';
import { BookingFormGroup, BookingFormService } from './booking-form.service';

@Component({
  selector: 'jhi-booking-update',
  templateUrl: './booking-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookingUpdateComponent implements OnInit {
  isSaving = false;
  booking: IBooking | null = null;
  bookingStatusValues = Object.keys(BookingStatus);
  paymentStatusValues = Object.keys(PaymentStatus);
  paymentMethodValues = Object.keys(PaymentMethod);

  promotionsSharedCollection: IPromotion[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected bookingService = inject(BookingService);
  protected bookingFormService = inject(BookingFormService);
  protected promotionService = inject(PromotionService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookingFormGroup = this.bookingFormService.createBookingFormGroup();

  comparePromotion = (o1: IPromotion | null, o2: IPromotion | null): boolean => this.promotionService.comparePromotion(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ booking }) => {
      this.booking = booking;
      if (booking) {
        this.updateForm(booking);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const booking = this.bookingFormService.getBooking(this.editForm);
    if (booking.id !== null) {
      this.subscribeToSaveResponse(this.bookingService.update(booking));
    } else {
      this.subscribeToSaveResponse(this.bookingService.create(booking));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBooking>>): void {
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

  protected updateForm(booking: IBooking): void {
    this.booking = booking;
    this.bookingFormService.resetForm(this.editForm, booking);

    this.promotionsSharedCollection = this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(
      this.promotionsSharedCollection,
      booking.promotion,
    );
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      booking.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.promotionService
      .query()
      .pipe(map((res: HttpResponse<IPromotion[]>) => res.body ?? []))
      .pipe(
        map((promotions: IPromotion[]) =>
          this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, this.booking?.promotion),
        ),
      )
      .subscribe((promotions: IPromotion[]) => (this.promotionsSharedCollection = promotions));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.booking?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}
