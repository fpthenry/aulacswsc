import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../site-setting.test-samples';

import { SiteSettingFormService } from './site-setting-form.service';

describe('SiteSetting Form Service', () => {
  let service: SiteSettingFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SiteSettingFormService);
  });

  describe('Service methods', () => {
    describe('createSiteSettingFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSiteSettingFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            siteName: expect.any(Object),
            logoUrl: expect.any(Object),
            sloganVi: expect.any(Object),
            sloganEn: expect.any(Object),
            heroSubtitleVi: expect.any(Object),
            heroSubtitleEn: expect.any(Object),
            heroImageUrl: expect.any(Object),
            heroVideoUrl: expect.any(Object),
            heroVideoEnabled: expect.any(Object),
            backgroundMusicUrl: expect.any(Object),
            aboutTitleVi: expect.any(Object),
            aboutTitleEn: expect.any(Object),
            aboutContentVi: expect.any(Object),
            aboutContentEn: expect.any(Object),
            highlight1TitleVi: expect.any(Object),
            highlight1TitleEn: expect.any(Object),
            highlight1TextVi: expect.any(Object),
            highlight1TextEn: expect.any(Object),
            highlight2TitleVi: expect.any(Object),
            highlight2TitleEn: expect.any(Object),
            highlight2TextVi: expect.any(Object),
            highlight2TextEn: expect.any(Object),
            highlight3TitleVi: expect.any(Object),
            highlight3TitleEn: expect.any(Object),
            highlight3TextVi: expect.any(Object),
            highlight3TextEn: expect.any(Object),
            hotline: expect.any(Object),
            email: expect.any(Object),
            whatsappNumber: expect.any(Object),
            zaloNumber: expect.any(Object),
            messengerUrl: expect.any(Object),
            facebookUrl: expect.any(Object),
            instagramUrl: expect.any(Object),
            tiktokUrl: expect.any(Object),
            youtubeUrl: expect.any(Object),
            openingHoursVi: expect.any(Object),
            openingHoursEn: expect.any(Object),
            metaDescriptionVi: expect.any(Object),
            metaDescriptionEn: expect.any(Object),
            policyContentVi: expect.any(Object),
            policyContentEn: expect.any(Object),
          }),
        );
      });

      it('passing ISiteSetting should create a new form with FormGroup', () => {
        const formGroup = service.createSiteSettingFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            siteName: expect.any(Object),
            logoUrl: expect.any(Object),
            sloganVi: expect.any(Object),
            sloganEn: expect.any(Object),
            heroSubtitleVi: expect.any(Object),
            heroSubtitleEn: expect.any(Object),
            heroImageUrl: expect.any(Object),
            heroVideoUrl: expect.any(Object),
            heroVideoEnabled: expect.any(Object),
            backgroundMusicUrl: expect.any(Object),
            aboutTitleVi: expect.any(Object),
            aboutTitleEn: expect.any(Object),
            aboutContentVi: expect.any(Object),
            aboutContentEn: expect.any(Object),
            highlight1TitleVi: expect.any(Object),
            highlight1TitleEn: expect.any(Object),
            highlight1TextVi: expect.any(Object),
            highlight1TextEn: expect.any(Object),
            highlight2TitleVi: expect.any(Object),
            highlight2TitleEn: expect.any(Object),
            highlight2TextVi: expect.any(Object),
            highlight2TextEn: expect.any(Object),
            highlight3TitleVi: expect.any(Object),
            highlight3TitleEn: expect.any(Object),
            highlight3TextVi: expect.any(Object),
            highlight3TextEn: expect.any(Object),
            hotline: expect.any(Object),
            email: expect.any(Object),
            whatsappNumber: expect.any(Object),
            zaloNumber: expect.any(Object),
            messengerUrl: expect.any(Object),
            facebookUrl: expect.any(Object),
            instagramUrl: expect.any(Object),
            tiktokUrl: expect.any(Object),
            youtubeUrl: expect.any(Object),
            openingHoursVi: expect.any(Object),
            openingHoursEn: expect.any(Object),
            metaDescriptionVi: expect.any(Object),
            metaDescriptionEn: expect.any(Object),
            policyContentVi: expect.any(Object),
            policyContentEn: expect.any(Object),
          }),
        );
      });
    });

    describe('getSiteSetting', () => {
      it('should return NewSiteSetting for default SiteSetting initial value', () => {
        const formGroup = service.createSiteSettingFormGroup(sampleWithNewData);

        const siteSetting = service.getSiteSetting(formGroup) as any;

        expect(siteSetting).toMatchObject(sampleWithNewData);
      });

      it('should return NewSiteSetting for empty SiteSetting initial value', () => {
        const formGroup = service.createSiteSettingFormGroup();

        const siteSetting = service.getSiteSetting(formGroup) as any;

        expect(siteSetting).toMatchObject({});
      });

      it('should return ISiteSetting', () => {
        const formGroup = service.createSiteSettingFormGroup(sampleWithRequiredData);

        const siteSetting = service.getSiteSetting(formGroup) as any;

        expect(siteSetting).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISiteSetting should not enable id FormControl', () => {
        const formGroup = service.createSiteSettingFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSiteSetting should disable id FormControl', () => {
        const formGroup = service.createSiteSettingFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
