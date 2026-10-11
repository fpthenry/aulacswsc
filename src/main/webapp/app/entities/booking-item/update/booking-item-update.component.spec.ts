import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISpaService } from 'app/entities/spa-service/spa-service.model';
import { SpaServiceService } from 'app/entities/spa-service/service/spa-service.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { IBooking } from 'app/entities/booking/booking.model';
import { BookingService } from 'app/entities/booking/service/booking.service';
import { IBookingItem } from '../booking-item.model';
import { BookingItemService } from '../service/booking-item.service';
import { BookingItemFormService } from './booking-item-form.service';

import { BookingItemUpdateComponent } from './booking-item-update.component';

describe('BookingItem Management Update Component', () => {
  let comp: BookingItemUpdateComponent;
  let fixture: ComponentFixture<BookingItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookingItemFormService: BookingItemFormService;
  let bookingItemService: BookingItemService;
  let spaServiceService: SpaServiceService;
  let staffService: StaffService;
  let bookingService: BookingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BookingItemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookingItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookingItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookingItemFormService = TestBed.inject(BookingItemFormService);
    bookingItemService = TestBed.inject(BookingItemService);
    spaServiceService = TestBed.inject(SpaServiceService);
    staffService = TestBed.inject(StaffService);
    bookingService = TestBed.inject(BookingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call SpaService query and add missing value', () => {
      const bookingItem: IBookingItem = { id: 28054 };
      const service: ISpaService = { id: 19409 };
      bookingItem.service = service;

      const spaServiceCollection: ISpaService[] = [{ id: 19409 }];
      jest.spyOn(spaServiceService, 'query').mockReturnValue(of(new HttpResponse({ body: spaServiceCollection })));
      const additionalSpaServices = [service];
      const expectedCollection: ISpaService[] = [...additionalSpaServices, ...spaServiceCollection];
      jest.spyOn(spaServiceService, 'addSpaServiceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      expect(spaServiceService.query).toHaveBeenCalled();
      expect(spaServiceService.addSpaServiceToCollectionIfMissing).toHaveBeenCalledWith(
        spaServiceCollection,
        ...additionalSpaServices.map(expect.objectContaining),
      );
      expect(comp.spaServicesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Staff query and add missing value', () => {
      const bookingItem: IBookingItem = { id: 28054 };
      const staff: IStaff = { id: 10841 };
      bookingItem.staff = staff;

      const staffCollection: IStaff[] = [{ id: 10841 }];
      jest.spyOn(staffService, 'query').mockReturnValue(of(new HttpResponse({ body: staffCollection })));
      const additionalStaff = [staff];
      const expectedCollection: IStaff[] = [...additionalStaff, ...staffCollection];
      jest.spyOn(staffService, 'addStaffToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      expect(staffService.query).toHaveBeenCalled();
      expect(staffService.addStaffToCollectionIfMissing).toHaveBeenCalledWith(
        staffCollection,
        ...additionalStaff.map(expect.objectContaining),
      );
      expect(comp.staffSharedCollection).toEqual(expectedCollection);
    });

    it('should call Booking query and add missing value', () => {
      const bookingItem: IBookingItem = { id: 28054 };
      const booking: IBooking = { id: 1408 };
      bookingItem.booking = booking;

      const bookingCollection: IBooking[] = [{ id: 1408 }];
      jest.spyOn(bookingService, 'query').mockReturnValue(of(new HttpResponse({ body: bookingCollection })));
      const additionalBookings = [booking];
      const expectedCollection: IBooking[] = [...additionalBookings, ...bookingCollection];
      jest.spyOn(bookingService, 'addBookingToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      expect(bookingService.query).toHaveBeenCalled();
      expect(bookingService.addBookingToCollectionIfMissing).toHaveBeenCalledWith(
        bookingCollection,
        ...additionalBookings.map(expect.objectContaining),
      );
      expect(comp.bookingsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const bookingItem: IBookingItem = { id: 28054 };
      const service: ISpaService = { id: 19409 };
      bookingItem.service = service;
      const staff: IStaff = { id: 10841 };
      bookingItem.staff = staff;
      const booking: IBooking = { id: 1408 };
      bookingItem.booking = booking;

      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      expect(comp.spaServicesSharedCollection).toContainEqual(service);
      expect(comp.staffSharedCollection).toContainEqual(staff);
      expect(comp.bookingsSharedCollection).toContainEqual(booking);
      expect(comp.bookingItem).toEqual(bookingItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingItem>>();
      const bookingItem = { id: 7381 };
      jest.spyOn(bookingItemFormService, 'getBookingItem').mockReturnValue(bookingItem);
      jest.spyOn(bookingItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookingItem }));
      saveSubject.complete();

      // THEN
      expect(bookingItemFormService.getBookingItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookingItemService.update).toHaveBeenCalledWith(expect.objectContaining(bookingItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingItem>>();
      const bookingItem = { id: 7381 };
      jest.spyOn(bookingItemFormService, 'getBookingItem').mockReturnValue({ id: null });
      jest.spyOn(bookingItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookingItem }));
      saveSubject.complete();

      // THEN
      expect(bookingItemFormService.getBookingItem).toHaveBeenCalled();
      expect(bookingItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookingItem>>();
      const bookingItem = { id: 7381 };
      jest.spyOn(bookingItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookingItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSpaService', () => {
      it('should forward to spaServiceService', () => {
        const entity = { id: 19409 };
        const entity2 = { id: 13678 };
        jest.spyOn(spaServiceService, 'compareSpaService');
        comp.compareSpaService(entity, entity2);
        expect(spaServiceService.compareSpaService).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStaff', () => {
      it('should forward to staffService', () => {
        const entity = { id: 10841 };
        const entity2 = { id: 8575 };
        jest.spyOn(staffService, 'compareStaff');
        comp.compareStaff(entity, entity2);
        expect(staffService.compareStaff).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBooking', () => {
      it('should forward to bookingService', () => {
        const entity = { id: 1408 };
        const entity2 = { id: 4697 };
        jest.spyOn(bookingService, 'compareBooking');
        comp.compareBooking(entity, entity2);
        expect(bookingService.compareBooking).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
