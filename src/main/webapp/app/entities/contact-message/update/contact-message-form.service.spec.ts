import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../contact-message.test-samples';

import { ContactMessageFormService } from './contact-message-form.service';

describe('ContactMessage Form Service', () => {
  let service: ContactMessageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactMessageFormService);
  });

  describe('Service methods', () => {
    describe('createContactMessageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createContactMessageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            phone: expect.any(Object),
            email: expect.any(Object),
            message: expect.any(Object),
            handled: expect.any(Object),
            createdDate: expect.any(Object),
          }),
        );
      });

      it('passing IContactMessage should create a new form with FormGroup', () => {
        const formGroup = service.createContactMessageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            phone: expect.any(Object),
            email: expect.any(Object),
            message: expect.any(Object),
            handled: expect.any(Object),
            createdDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getContactMessage', () => {
      it('should return NewContactMessage for default ContactMessage initial value', () => {
        const formGroup = service.createContactMessageFormGroup(sampleWithNewData);

        const contactMessage = service.getContactMessage(formGroup) as any;

        expect(contactMessage).toMatchObject(sampleWithNewData);
      });

      it('should return NewContactMessage for empty ContactMessage initial value', () => {
        const formGroup = service.createContactMessageFormGroup();

        const contactMessage = service.getContactMessage(formGroup) as any;

        expect(contactMessage).toMatchObject({});
      });

      it('should return IContactMessage', () => {
        const formGroup = service.createContactMessageFormGroup(sampleWithRequiredData);

        const contactMessage = service.getContactMessage(formGroup) as any;

        expect(contactMessage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IContactMessage should not enable id FormControl', () => {
        const formGroup = service.createContactMessageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewContactMessage should disable id FormControl', () => {
        const formGroup = service.createContactMessageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
