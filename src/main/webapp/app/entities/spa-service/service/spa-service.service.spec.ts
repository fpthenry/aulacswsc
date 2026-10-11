import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISpaService } from '../spa-service.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../spa-service.test-samples';

import { SpaServiceService } from './spa-service.service';

const requireRestSample: ISpaService = {
  ...sampleWithRequiredData,
};

describe('SpaService Service', () => {
  let service: SpaServiceService;
  let httpMock: HttpTestingController;
  let expectedResult: ISpaService | ISpaService[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SpaServiceService);
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

    it('should create a SpaService', () => {
      const spaService = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(spaService).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SpaService', () => {
      const spaService = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(spaService).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SpaService', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SpaService', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SpaService', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSpaServiceToCollectionIfMissing', () => {
      it('should add a SpaService to an empty array', () => {
        const spaService: ISpaService = sampleWithRequiredData;
        expectedResult = service.addSpaServiceToCollectionIfMissing([], spaService);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spaService);
      });

      it('should not add a SpaService to an array that contains it', () => {
        const spaService: ISpaService = sampleWithRequiredData;
        const spaServiceCollection: ISpaService[] = [
          {
            ...spaService,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSpaServiceToCollectionIfMissing(spaServiceCollection, spaService);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SpaService to an array that doesn't contain it", () => {
        const spaService: ISpaService = sampleWithRequiredData;
        const spaServiceCollection: ISpaService[] = [sampleWithPartialData];
        expectedResult = service.addSpaServiceToCollectionIfMissing(spaServiceCollection, spaService);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spaService);
      });

      it('should add only unique SpaService to an array', () => {
        const spaServiceArray: ISpaService[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const spaServiceCollection: ISpaService[] = [sampleWithRequiredData];
        expectedResult = service.addSpaServiceToCollectionIfMissing(spaServiceCollection, ...spaServiceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spaService: ISpaService = sampleWithRequiredData;
        const spaService2: ISpaService = sampleWithPartialData;
        expectedResult = service.addSpaServiceToCollectionIfMissing([], spaService, spaService2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spaService);
        expect(expectedResult).toContain(spaService2);
      });

      it('should accept null and undefined values', () => {
        const spaService: ISpaService = sampleWithRequiredData;
        expectedResult = service.addSpaServiceToCollectionIfMissing([], null, spaService, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spaService);
      });

      it('should return initial array if no SpaService is added', () => {
        const spaServiceCollection: ISpaService[] = [sampleWithRequiredData];
        expectedResult = service.addSpaServiceToCollectionIfMissing(spaServiceCollection, undefined, null);
        expect(expectedResult).toEqual(spaServiceCollection);
      });
    });

    describe('compareSpaService', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSpaService(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19409 };
        const entity2 = null;

        const compareResult1 = service.compareSpaService(entity1, entity2);
        const compareResult2 = service.compareSpaService(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19409 };
        const entity2 = { id: 13678 };

        const compareResult1 = service.compareSpaService(entity1, entity2);
        const compareResult2 = service.compareSpaService(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19409 };
        const entity2 = { id: 19409 };

        const compareResult1 = service.compareSpaService(entity1, entity2);
        const compareResult2 = service.compareSpaService(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
