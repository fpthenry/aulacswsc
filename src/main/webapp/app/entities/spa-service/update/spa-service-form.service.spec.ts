import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../spa-service.test-samples';

import { SpaServiceFormService } from './spa-service-form.service';

describe('SpaService Form Service', () => {
  let service: SpaServiceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpaServiceFormService);
  });

  describe('Service methods', () => {
    describe('createSpaServiceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSpaServiceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            shortDescriptionVi: expect.any(Object),
            shortDescriptionEn: expect.any(Object),
            descriptionVi: expect.any(Object),
            descriptionEn: expect.any(Object),
            benefitsVi: expect.any(Object),
            benefitsEn: expect.any(Object),
            notesVi: expect.any(Object),
            notesEn: expect.any(Object),
            price: expect.any(Object),
            salePrice: expect.any(Object),
            priceMax: expect.any(Object),
            durationMinutes: expect.any(Object),
            imageUrl: expect.any(Object),
            featured: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
            category: expect.any(Object),
            staffs: expect.any(Object),
          }),
        );
      });

      it('passing ISpaService should create a new form with FormGroup', () => {
        const formGroup = service.createSpaServiceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            shortDescriptionVi: expect.any(Object),
            shortDescriptionEn: expect.any(Object),
            descriptionVi: expect.any(Object),
            descriptionEn: expect.any(Object),
            benefitsVi: expect.any(Object),
            benefitsEn: expect.any(Object),
            notesVi: expect.any(Object),
            notesEn: expect.any(Object),
            price: expect.any(Object),
            salePrice: expect.any(Object),
            priceMax: expect.any(Object),
            durationMinutes: expect.any(Object),
            imageUrl: expect.any(Object),
            featured: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
            category: expect.any(Object),
            staffs: expect.any(Object),
          }),
        );
      });
    });

    describe('getSpaService', () => {
      it('should return NewSpaService for default SpaService initial value', () => {
        const formGroup = service.createSpaServiceFormGroup(sampleWithNewData);

        const spaService = service.getSpaService(formGroup) as any;

        expect(spaService).toMatchObject(sampleWithNewData);
      });

      it('should return NewSpaService for empty SpaService initial value', () => {
        const formGroup = service.createSpaServiceFormGroup();

        const spaService = service.getSpaService(formGroup) as any;

        expect(spaService).toMatchObject({});
      });

      it('should return ISpaService', () => {
        const formGroup = service.createSpaServiceFormGroup(sampleWithRequiredData);

        const spaService = service.getSpaService(formGroup) as any;

        expect(spaService).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISpaService should not enable id FormControl', () => {
        const formGroup = service.createSpaServiceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSpaService should disable id FormControl', () => {
        const formGroup = service.createSpaServiceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
