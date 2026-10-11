import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBookingItem } from '../booking-item.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../booking-item.test-samples';

import { BookingItemService } from './booking-item.service';

const requireRestSample: IBookingItem = {
  ...sampleWithRequiredData,
};

describe('BookingItem Service', () => {
  let service: BookingItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookingItem | IBookingItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookingItemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BookingItem', () => {
      const bookingItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookingItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookingItem', () => {
      const bookingItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookingItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookingItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookingItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookingItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBookingItemToCollectionIfMissing', () => {
      it('should add a BookingItem to an empty array', () => {
        const bookingItem: IBookingItem = sampleWithRequiredData;
        expectedResult = service.addBookingItemToCollectionIfMissing([], bookingItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookingItem);
      });

      it('should not add a BookingItem to an array that contains it', () => {
        const bookingItem: IBookingItem = sampleWithRequiredData;
        const bookingItemCollection: IBookingItem[] = [
          {
            ...bookingItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookingItemToCollectionIfMissing(bookingItemCollection, bookingItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookingItem to an array that doesn't contain it", () => {
        const bookingItem: IBookingItem = sampleWithRequiredData;
        const bookingItemCollection: IBookingItem[] = [sampleWithPartialData];
        expectedResult = service.addBookingItemToCollectionIfMissing(bookingItemCollection, bookingItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookingItem);
      });

      it('should add only unique BookingItem to an array', () => {
        const bookingItemArray: IBookingItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookingItemCollection: IBookingItem[] = [sampleWithRequiredData];
        expectedResult = service.addBookingItemToCollectionIfMissing(bookingItemCollection, ...bookingItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookingItem: IBookingItem = sampleWithRequiredData;
        const bookingItem2: IBookingItem = sampleWithPartialData;
        expectedResult = service.addBookingItemToCollectionIfMissing([], bookingItem, bookingItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookingItem);
        expect(expectedResult).toContain(bookingItem2);
      });

      it('should accept null and undefined values', () => {
        const bookingItem: IBookingItem = sampleWithRequiredData;
        expectedResult = service.addBookingItemToCollectionIfMissing([], null, bookingItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookingItem);
      });

      it('should return initial array if no BookingItem is added', () => {
        const bookingItemCollection: IBookingItem[] = [sampleWithRequiredData];
        expectedResult = service.addBookingItemToCollectionIfMissing(bookingItemCollection, undefined, null);
        expect(expectedResult).toEqual(bookingItemCollection);
      });
    });

    describe('compareBookingItem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookingItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7381 };
        const entity2 = null;

        const compareResult1 = service.compareBookingItem(entity1, entity2);
        const compareResult2 = service.compareBookingItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7381 };
        const entity2 = { id: 28054 };

        const compareResult1 = service.compareBookingItem(entity1, entity2);
        const compareResult2 = service.compareBookingItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 7381 };
        const entity2 = { id: 7381 };

        const compareResult1 = service.compareBookingItem(entity1, entity2);
        const compareResult2 = service.compareBookingItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
