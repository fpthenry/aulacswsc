import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../service-category.test-samples';

import { ServiceCategoryFormService } from './service-category-form.service';

describe('ServiceCategory Form Service', () => {
  let service: ServiceCategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServiceCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createServiceCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createServiceCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            descriptionVi: expect.any(Object),
            descriptionEn: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });

      it('passing IServiceCategory should create a new form with FormGroup', () => {
        const formGroup = service.createServiceCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            descriptionVi: expect.any(Object),
            descriptionEn: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });
    });

    describe('getServiceCategory', () => {
      it('should return NewServiceCategory for default ServiceCategory initial value', () => {
        const formGroup = service.createServiceCategoryFormGroup(sampleWithNewData);

        const serviceCategory = service.getServiceCategory(formGroup) as any;

        expect(serviceCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewServiceCategory for empty ServiceCategory initial value', () => {
        const formGroup = service.createServiceCategoryFormGroup();

        const serviceCategory = service.getServiceCategory(formGroup) as any;

        expect(serviceCategory).toMatchObject({});
      });

      it('should return IServiceCategory', () => {
        const formGroup = service.createServiceCategoryFormGroup(sampleWithRequiredData);

        const serviceCategory = service.getServiceCategory(formGroup) as any;

        expect(serviceCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IServiceCategory should not enable id FormControl', () => {
        const formGroup = service.createServiceCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewServiceCategory should disable id FormControl', () => {
        const formGroup = service.createServiceCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
