import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../booking-item.test-samples';

import { BookingItemFormService } from './booking-item-form.service';

describe('BookingItem Form Service', () => {
  let service: BookingItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookingItemFormService);
  });

  describe('Service methods', () => {
    describe('createBookingItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookingItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            price: expect.any(Object),
            quantity: expect.any(Object),
            durationMinutes: expect.any(Object),
            service: expect.any(Object),
            staff: expect.any(Object),
            booking: expect.any(Object),
          }),
        );
      });

      it('passing IBookingItem should create a new form with FormGroup', () => {
        const formGroup = service.createBookingItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            price: expect.any(Object),
            quantity: expect.any(Object),
            durationMinutes: expect.any(Object),
            service: expect.any(Object),
            staff: expect.any(Object),
            booking: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookingItem', () => {
      it('should return NewBookingItem for default BookingItem initial value', () => {
        const formGroup = service.createBookingItemFormGroup(sampleWithNewData);

        const bookingItem = service.getBookingItem(formGroup) as any;

        expect(bookingItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookingItem for empty BookingItem initial value', () => {
        const formGroup = service.createBookingItemFormGroup();

        const bookingItem = service.getBookingItem(formGroup) as any;

        expect(bookingItem).toMatchObject({});
      });

      it('should return IBookingItem', () => {
        const formGroup = service.createBookingItemFormGroup(sampleWithRequiredData);

        const bookingItem = service.getBookingItem(formGroup) as any;

        expect(bookingItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookingItem should not enable id FormControl', () => {
        const formGroup = service.createBookingItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookingItem should disable id FormControl', () => {
        const formGroup = service.createBookingItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
