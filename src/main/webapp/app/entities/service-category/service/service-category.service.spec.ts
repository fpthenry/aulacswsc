import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IServiceCategory } from '../service-category.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../service-category.test-samples';

import { ServiceCategoryService } from './service-category.service';

const requireRestSample: IServiceCategory = {
  ...sampleWithRequiredData,
};

describe('ServiceCategory Service', () => {
  let service: ServiceCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IServiceCategory | IServiceCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ServiceCategoryService);
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

    it('should create a ServiceCategory', () => {
      const serviceCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(serviceCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ServiceCategory', () => {
      const serviceCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(serviceCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ServiceCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ServiceCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ServiceCategory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addServiceCategoryToCollectionIfMissing', () => {
      it('should add a ServiceCategory to an empty array', () => {
        const serviceCategory: IServiceCategory = sampleWithRequiredData;
        expectedResult = service.addServiceCategoryToCollectionIfMissing([], serviceCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serviceCategory);
      });

      it('should not add a ServiceCategory to an array that contains it', () => {
        const serviceCategory: IServiceCategory = sampleWithRequiredData;
        const serviceCategoryCollection: IServiceCategory[] = [
          {
            ...serviceCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addServiceCategoryToCollectionIfMissing(serviceCategoryCollection, serviceCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ServiceCategory to an array that doesn't contain it", () => {
        const serviceCategory: IServiceCategory = sampleWithRequiredData;
        const serviceCategoryCollection: IServiceCategory[] = [sampleWithPartialData];
        expectedResult = service.addServiceCategoryToCollectionIfMissing(serviceCategoryCollection, serviceCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serviceCategory);
      });

      it('should add only unique ServiceCategory to an array', () => {
        const serviceCategoryArray: IServiceCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const serviceCategoryCollection: IServiceCategory[] = [sampleWithRequiredData];
        expectedResult = service.addServiceCategoryToCollectionIfMissing(serviceCategoryCollection, ...serviceCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const serviceCategory: IServiceCategory = sampleWithRequiredData;
        const serviceCategory2: IServiceCategory = sampleWithPartialData;
        expectedResult = service.addServiceCategoryToCollectionIfMissing([], serviceCategory, serviceCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(serviceCategory);
        expect(expectedResult).toContain(serviceCategory2);
      });

      it('should accept null and undefined values', () => {
        const serviceCategory: IServiceCategory = sampleWithRequiredData;
        expectedResult = service.addServiceCategoryToCollectionIfMissing([], null, serviceCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(serviceCategory);
      });

      it('should return initial array if no ServiceCategory is added', () => {
        const serviceCategoryCollection: IServiceCategory[] = [sampleWithRequiredData];
        expectedResult = service.addServiceCategoryToCollectionIfMissing(serviceCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(serviceCategoryCollection);
      });
    });

    describe('compareServiceCategory', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareServiceCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13984 };
        const entity2 = null;

        const compareResult1 = service.compareServiceCategory(entity1, entity2);
        const compareResult2 = service.compareServiceCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13984 };
        const entity2 = { id: 22395 };

        const compareResult1 = service.compareServiceCategory(entity1, entity2);
        const compareResult2 = service.compareServiceCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13984 };
        const entity2 = { id: 13984 };

        const compareResult1 = service.compareServiceCategory(entity1, entity2);
        const compareResult2 = service.compareServiceCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
