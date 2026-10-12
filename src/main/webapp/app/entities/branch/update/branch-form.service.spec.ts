import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../branch.test-samples';

import { BranchFormService } from './branch-form.service';

describe('Branch Form Service', () => {
  let service: BranchFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BranchFormService);
  });

  describe('Service methods', () => {
    describe('createBranchFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBranchFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            addressVi: expect.any(Object),
            addressEn: expect.any(Object),
            phone: expect.any(Object),
            whatsapp: expect.any(Object),
            zalo: expect.any(Object),
            email: expect.any(Object),
            openTime: expect.any(Object),
            closeTime: expect.any(Object),
            lastBookingTime: expect.any(Object),
            capacity: expect.any(Object),
            mapEmbedUrl: expect.any(Object),
            mapDirectionUrl: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });

      it('passing IBranch should create a new form with FormGroup', () => {
        const formGroup = service.createBranchFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameVi: expect.any(Object),
            nameEn: expect.any(Object),
            slug: expect.any(Object),
            addressVi: expect.any(Object),
            addressEn: expect.any(Object),
            phone: expect.any(Object),
            whatsapp: expect.any(Object),
            zalo: expect.any(Object),
            email: expect.any(Object),
            openTime: expect.any(Object),
            closeTime: expect.any(Object),
            lastBookingTime: expect.any(Object),
            capacity: expect.any(Object),
            mapEmbedUrl: expect.any(Object),
            mapDirectionUrl: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            active: expect.any(Object),
          }),
        );
      });
    });

    describe('getBranch', () => {
      it('should return NewBranch for default Branch initial value', () => {
        const formGroup = service.createBranchFormGroup(sampleWithNewData);

        const branch = service.getBranch(formGroup) as any;

        expect(branch).toMatchObject(sampleWithNewData);
      });

      it('should return NewBranch for empty Branch initial value', () => {
        const formGroup = service.createBranchFormGroup();

        const branch = service.getBranch(formGroup) as any;

        expect(branch).toMatchObject({});
      });

      it('should return IBranch', () => {
        const formGroup = service.createBranchFormGroup(sampleWithRequiredData);

        const branch = service.getBranch(formGroup) as any;

        expect(branch).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBranch should not enable id FormControl', () => {
        const formGroup = service.createBranchFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBranch should disable id FormControl', () => {
        const formGroup = service.createBranchFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
