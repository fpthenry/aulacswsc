import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBranch } from '../branch.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../branch.test-samples';

import { BranchService } from './branch.service';

const requireRestSample: IBranch = {
  ...sampleWithRequiredData,
};

describe('Branch Service', () => {
  let service: BranchService;
  let httpMock: HttpTestingController;
  let expectedResult: IBranch | IBranch[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BranchService);
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

    it('should create a Branch', () => {
      const branch = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(branch).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Branch', () => {
      const branch = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(branch).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Branch', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Branch', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Branch', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBranchToCollectionIfMissing', () => {
      it('should add a Branch to an empty array', () => {
        const branch: IBranch = sampleWithRequiredData;
        expectedResult = service.addBranchToCollectionIfMissing([], branch);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(branch);
      });

      it('should not add a Branch to an array that contains it', () => {
        const branch: IBranch = sampleWithRequiredData;
        const branchCollection: IBranch[] = [
          {
            ...branch,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBranchToCollectionIfMissing(branchCollection, branch);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Branch to an array that doesn't contain it", () => {
        const branch: IBranch = sampleWithRequiredData;
        const branchCollection: IBranch[] = [sampleWithPartialData];
        expectedResult = service.addBranchToCollectionIfMissing(branchCollection, branch);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(branch);
      });

      it('should add only unique Branch to an array', () => {
        const branchArray: IBranch[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const branchCollection: IBranch[] = [sampleWithRequiredData];
        expectedResult = service.addBranchToCollectionIfMissing(branchCollection, ...branchArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const branch: IBranch = sampleWithRequiredData;
        const branch2: IBranch = sampleWithPartialData;
        expectedResult = service.addBranchToCollectionIfMissing([], branch, branch2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(branch);
        expect(expectedResult).toContain(branch2);
      });

      it('should accept null and undefined values', () => {
        const branch: IBranch = sampleWithRequiredData;
        expectedResult = service.addBranchToCollectionIfMissing([], null, branch, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(branch);
      });

      it('should return initial array if no Branch is added', () => {
        const branchCollection: IBranch[] = [sampleWithRequiredData];
        expectedResult = service.addBranchToCollectionIfMissing(branchCollection, undefined, null);
        expect(expectedResult).toEqual(branchCollection);
      });
    });

    describe('compareBranch', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBranch(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23953 };
        const entity2 = null;

        const compareResult1 = service.compareBranch(entity1, entity2);
        const compareResult2 = service.compareBranch(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23953 };
        const entity2 = { id: 1770 };

        const compareResult1 = service.compareBranch(entity1, entity2);
        const compareResult2 = service.compareBranch(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23953 };
        const entity2 = { id: 23953 };

        const compareResult1 = service.compareBranch(entity1, entity2);
        const compareResult2 = service.compareBranch(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
