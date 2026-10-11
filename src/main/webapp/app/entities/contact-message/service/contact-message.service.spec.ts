import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IContactMessage } from '../contact-message.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../contact-message.test-samples';

import { ContactMessageService, RestContactMessage } from './contact-message.service';

const requireRestSample: RestContactMessage = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
};

describe('ContactMessage Service', () => {
  let service: ContactMessageService;
  let httpMock: HttpTestingController;
  let expectedResult: IContactMessage | IContactMessage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ContactMessageService);
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

    it('should create a ContactMessage', () => {
      const contactMessage = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(contactMessage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ContactMessage', () => {
      const contactMessage = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(contactMessage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ContactMessage', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ContactMessage', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ContactMessage', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addContactMessageToCollectionIfMissing', () => {
      it('should add a ContactMessage to an empty array', () => {
        const contactMessage: IContactMessage = sampleWithRequiredData;
        expectedResult = service.addContactMessageToCollectionIfMissing([], contactMessage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contactMessage);
      });

      it('should not add a ContactMessage to an array that contains it', () => {
        const contactMessage: IContactMessage = sampleWithRequiredData;
        const contactMessageCollection: IContactMessage[] = [
          {
            ...contactMessage,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addContactMessageToCollectionIfMissing(contactMessageCollection, contactMessage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ContactMessage to an array that doesn't contain it", () => {
        const contactMessage: IContactMessage = sampleWithRequiredData;
        const contactMessageCollection: IContactMessage[] = [sampleWithPartialData];
        expectedResult = service.addContactMessageToCollectionIfMissing(contactMessageCollection, contactMessage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contactMessage);
      });

      it('should add only unique ContactMessage to an array', () => {
        const contactMessageArray: IContactMessage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const contactMessageCollection: IContactMessage[] = [sampleWithRequiredData];
        expectedResult = service.addContactMessageToCollectionIfMissing(contactMessageCollection, ...contactMessageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const contactMessage: IContactMessage = sampleWithRequiredData;
        const contactMessage2: IContactMessage = sampleWithPartialData;
        expectedResult = service.addContactMessageToCollectionIfMissing([], contactMessage, contactMessage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(contactMessage);
        expect(expectedResult).toContain(contactMessage2);
      });

      it('should accept null and undefined values', () => {
        const contactMessage: IContactMessage = sampleWithRequiredData;
        expectedResult = service.addContactMessageToCollectionIfMissing([], null, contactMessage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(contactMessage);
      });

      it('should return initial array if no ContactMessage is added', () => {
        const contactMessageCollection: IContactMessage[] = [sampleWithRequiredData];
        expectedResult = service.addContactMessageToCollectionIfMissing(contactMessageCollection, undefined, null);
        expect(expectedResult).toEqual(contactMessageCollection);
      });
    });

    describe('compareContactMessage', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareContactMessage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14574 };
        const entity2 = null;

        const compareResult1 = service.compareContactMessage(entity1, entity2);
        const compareResult2 = service.compareContactMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14574 };
        const entity2 = { id: 6827 };

        const compareResult1 = service.compareContactMessage(entity1, entity2);
        const compareResult2 = service.compareContactMessage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14574 };
        const entity2 = { id: 14574 };

        const compareResult1 = service.compareContactMessage(entity1, entity2);
        const compareResult2 = service.compareContactMessage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
