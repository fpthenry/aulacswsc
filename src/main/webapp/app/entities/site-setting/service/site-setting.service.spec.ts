import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISiteSetting } from '../site-setting.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../site-setting.test-samples';

import { SiteSettingService } from './site-setting.service';

const requireRestSample: ISiteSetting = {
  ...sampleWithRequiredData,
};

describe('SiteSetting Service', () => {
  let service: SiteSettingService;
  let httpMock: HttpTestingController;
  let expectedResult: ISiteSetting | ISiteSetting[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SiteSettingService);
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

    it('should create a SiteSetting', () => {
      const siteSetting = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(siteSetting).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SiteSetting', () => {
      const siteSetting = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(siteSetting).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SiteSetting', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SiteSetting', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SiteSetting', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSiteSettingToCollectionIfMissing', () => {
      it('should add a SiteSetting to an empty array', () => {
        const siteSetting: ISiteSetting = sampleWithRequiredData;
        expectedResult = service.addSiteSettingToCollectionIfMissing([], siteSetting);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(siteSetting);
      });

      it('should not add a SiteSetting to an array that contains it', () => {
        const siteSetting: ISiteSetting = sampleWithRequiredData;
        const siteSettingCollection: ISiteSetting[] = [
          {
            ...siteSetting,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSiteSettingToCollectionIfMissing(siteSettingCollection, siteSetting);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SiteSetting to an array that doesn't contain it", () => {
        const siteSetting: ISiteSetting = sampleWithRequiredData;
        const siteSettingCollection: ISiteSetting[] = [sampleWithPartialData];
        expectedResult = service.addSiteSettingToCollectionIfMissing(siteSettingCollection, siteSetting);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(siteSetting);
      });

      it('should add only unique SiteSetting to an array', () => {
        const siteSettingArray: ISiteSetting[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const siteSettingCollection: ISiteSetting[] = [sampleWithRequiredData];
        expectedResult = service.addSiteSettingToCollectionIfMissing(siteSettingCollection, ...siteSettingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const siteSetting: ISiteSetting = sampleWithRequiredData;
        const siteSetting2: ISiteSetting = sampleWithPartialData;
        expectedResult = service.addSiteSettingToCollectionIfMissing([], siteSetting, siteSetting2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(siteSetting);
        expect(expectedResult).toContain(siteSetting2);
      });

      it('should accept null and undefined values', () => {
        const siteSetting: ISiteSetting = sampleWithRequiredData;
        expectedResult = service.addSiteSettingToCollectionIfMissing([], null, siteSetting, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(siteSetting);
      });

      it('should return initial array if no SiteSetting is added', () => {
        const siteSettingCollection: ISiteSetting[] = [sampleWithRequiredData];
        expectedResult = service.addSiteSettingToCollectionIfMissing(siteSettingCollection, undefined, null);
        expect(expectedResult).toEqual(siteSettingCollection);
      });
    });

    describe('compareSiteSetting', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSiteSetting(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19433 };
        const entity2 = null;

        const compareResult1 = service.compareSiteSetting(entity1, entity2);
        const compareResult2 = service.compareSiteSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19433 };
        const entity2 = { id: 12335 };

        const compareResult1 = service.compareSiteSetting(entity1, entity2);
        const compareResult2 = service.compareSiteSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19433 };
        const entity2 = { id: 19433 };

        const compareResult1 = service.compareSiteSetting(entity1, entity2);
        const compareResult2 = service.compareSiteSetting(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
