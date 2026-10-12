import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISiteSetting, NewSiteSetting } from '../site-setting.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISiteSetting for edit and NewSiteSettingFormGroupInput for create.
 */
type SiteSettingFormGroupInput = ISiteSetting | PartialWithRequiredKeyOf<NewSiteSetting>;

type SiteSettingFormDefaults = Pick<NewSiteSetting, 'id' | 'heroVideoEnabled'>;

type SiteSettingFormGroupContent = {
  id: FormControl<ISiteSetting['id'] | NewSiteSetting['id']>;
  siteName: FormControl<ISiteSetting['siteName']>;
  logoUrl: FormControl<ISiteSetting['logoUrl']>;
  sloganVi: FormControl<ISiteSetting['sloganVi']>;
  sloganEn: FormControl<ISiteSetting['sloganEn']>;
  heroSubtitleVi: FormControl<ISiteSetting['heroSubtitleVi']>;
  heroSubtitleEn: FormControl<ISiteSetting['heroSubtitleEn']>;
  heroImageUrl: FormControl<ISiteSetting['heroImageUrl']>;
  heroVideoUrl: FormControl<ISiteSetting['heroVideoUrl']>;
  heroSlides: FormControl<ISiteSetting['heroSlides']>;
  heroVideoEnabled: FormControl<ISiteSetting['heroVideoEnabled']>;
  backgroundMusicUrl: FormControl<ISiteSetting['backgroundMusicUrl']>;
  aboutTitleVi: FormControl<ISiteSetting['aboutTitleVi']>;
  aboutTitleEn: FormControl<ISiteSetting['aboutTitleEn']>;
  aboutContentVi: FormControl<ISiteSetting['aboutContentVi']>;
  aboutContentEn: FormControl<ISiteSetting['aboutContentEn']>;
  highlight1TitleVi: FormControl<ISiteSetting['highlight1TitleVi']>;
  highlight1TitleEn: FormControl<ISiteSetting['highlight1TitleEn']>;
  highlight1TextVi: FormControl<ISiteSetting['highlight1TextVi']>;
  highlight1TextEn: FormControl<ISiteSetting['highlight1TextEn']>;
  highlight2TitleVi: FormControl<ISiteSetting['highlight2TitleVi']>;
  highlight2TitleEn: FormControl<ISiteSetting['highlight2TitleEn']>;
  highlight2TextVi: FormControl<ISiteSetting['highlight2TextVi']>;
  highlight2TextEn: FormControl<ISiteSetting['highlight2TextEn']>;
  highlight3TitleVi: FormControl<ISiteSetting['highlight3TitleVi']>;
  highlight3TitleEn: FormControl<ISiteSetting['highlight3TitleEn']>;
  highlight3TextVi: FormControl<ISiteSetting['highlight3TextVi']>;
  highlight3TextEn: FormControl<ISiteSetting['highlight3TextEn']>;
  hotline: FormControl<ISiteSetting['hotline']>;
  email: FormControl<ISiteSetting['email']>;
  whatsappNumber: FormControl<ISiteSetting['whatsappNumber']>;
  zaloNumber: FormControl<ISiteSetting['zaloNumber']>;
  messengerUrl: FormControl<ISiteSetting['messengerUrl']>;
  facebookUrl: FormControl<ISiteSetting['facebookUrl']>;
  instagramUrl: FormControl<ISiteSetting['instagramUrl']>;
  tiktokUrl: FormControl<ISiteSetting['tiktokUrl']>;
  youtubeUrl: FormControl<ISiteSetting['youtubeUrl']>;
  openingHoursVi: FormControl<ISiteSetting['openingHoursVi']>;
  openingHoursEn: FormControl<ISiteSetting['openingHoursEn']>;
  metaDescriptionVi: FormControl<ISiteSetting['metaDescriptionVi']>;
  metaDescriptionEn: FormControl<ISiteSetting['metaDescriptionEn']>;
  policyContentVi: FormControl<ISiteSetting['policyContentVi']>;
  policyContentEn: FormControl<ISiteSetting['policyContentEn']>;
};

export type SiteSettingFormGroup = FormGroup<SiteSettingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SiteSettingFormService {
  createSiteSettingFormGroup(siteSetting: SiteSettingFormGroupInput = { id: null }): SiteSettingFormGroup {
    const siteSettingRawValue = {
      ...this.getFormDefaults(),
      ...siteSetting,
    };
    return new FormGroup<SiteSettingFormGroupContent>({
      id: new FormControl(
        { value: siteSettingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      siteName: new FormControl(siteSettingRawValue.siteName, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      logoUrl: new FormControl(siteSettingRawValue.logoUrl, {
        validators: [Validators.maxLength(500)],
      }),
      sloganVi: new FormControl(siteSettingRawValue.sloganVi, {
        validators: [Validators.maxLength(300)],
      }),
      sloganEn: new FormControl(siteSettingRawValue.sloganEn, {
        validators: [Validators.maxLength(300)],
      }),
      heroSubtitleVi: new FormControl(siteSettingRawValue.heroSubtitleVi, {
        validators: [Validators.maxLength(500)],
      }),
      heroSubtitleEn: new FormControl(siteSettingRawValue.heroSubtitleEn, {
        validators: [Validators.maxLength(500)],
      }),
      heroImageUrl: new FormControl(siteSettingRawValue.heroImageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      heroVideoUrl: new FormControl(siteSettingRawValue.heroVideoUrl, {
        validators: [Validators.maxLength(500)],
      }),
      heroVideoEnabled: new FormControl(siteSettingRawValue.heroVideoEnabled),
      heroSlides: new FormControl(siteSettingRawValue.heroSlides),
      backgroundMusicUrl: new FormControl(siteSettingRawValue.backgroundMusicUrl, {
        validators: [Validators.maxLength(500)],
      }),
      aboutTitleVi: new FormControl(siteSettingRawValue.aboutTitleVi, {
        validators: [Validators.maxLength(300)],
      }),
      aboutTitleEn: new FormControl(siteSettingRawValue.aboutTitleEn, {
        validators: [Validators.maxLength(300)],
      }),
      aboutContentVi: new FormControl(siteSettingRawValue.aboutContentVi),
      aboutContentEn: new FormControl(siteSettingRawValue.aboutContentEn),
      highlight1TitleVi: new FormControl(siteSettingRawValue.highlight1TitleVi, {
        validators: [Validators.maxLength(150)],
      }),
      highlight1TitleEn: new FormControl(siteSettingRawValue.highlight1TitleEn, {
        validators: [Validators.maxLength(150)],
      }),
      highlight1TextVi: new FormControl(siteSettingRawValue.highlight1TextVi, {
        validators: [Validators.maxLength(500)],
      }),
      highlight1TextEn: new FormControl(siteSettingRawValue.highlight1TextEn, {
        validators: [Validators.maxLength(500)],
      }),
      highlight2TitleVi: new FormControl(siteSettingRawValue.highlight2TitleVi, {
        validators: [Validators.maxLength(150)],
      }),
      highlight2TitleEn: new FormControl(siteSettingRawValue.highlight2TitleEn, {
        validators: [Validators.maxLength(150)],
      }),
      highlight2TextVi: new FormControl(siteSettingRawValue.highlight2TextVi, {
        validators: [Validators.maxLength(500)],
      }),
      highlight2TextEn: new FormControl(siteSettingRawValue.highlight2TextEn, {
        validators: [Validators.maxLength(500)],
      }),
      highlight3TitleVi: new FormControl(siteSettingRawValue.highlight3TitleVi, {
        validators: [Validators.maxLength(150)],
      }),
      highlight3TitleEn: new FormControl(siteSettingRawValue.highlight3TitleEn, {
        validators: [Validators.maxLength(150)],
      }),
      highlight3TextVi: new FormControl(siteSettingRawValue.highlight3TextVi, {
        validators: [Validators.maxLength(500)],
      }),
      highlight3TextEn: new FormControl(siteSettingRawValue.highlight3TextEn, {
        validators: [Validators.maxLength(500)],
      }),
      hotline: new FormControl(siteSettingRawValue.hotline, {
        validators: [Validators.maxLength(20)],
      }),
      email: new FormControl(siteSettingRawValue.email, {
        validators: [Validators.maxLength(150)],
      }),
      whatsappNumber: new FormControl(siteSettingRawValue.whatsappNumber, {
        validators: [Validators.maxLength(20)],
      }),
      zaloNumber: new FormControl(siteSettingRawValue.zaloNumber, {
        validators: [Validators.maxLength(20)],
      }),
      messengerUrl: new FormControl(siteSettingRawValue.messengerUrl, {
        validators: [Validators.maxLength(300)],
      }),
      facebookUrl: new FormControl(siteSettingRawValue.facebookUrl, {
        validators: [Validators.maxLength(300)],
      }),
      instagramUrl: new FormControl(siteSettingRawValue.instagramUrl, {
        validators: [Validators.maxLength(300)],
      }),
      tiktokUrl: new FormControl(siteSettingRawValue.tiktokUrl, {
        validators: [Validators.maxLength(300)],
      }),
      youtubeUrl: new FormControl(siteSettingRawValue.youtubeUrl, {
        validators: [Validators.maxLength(300)],
      }),
      openingHoursVi: new FormControl(siteSettingRawValue.openingHoursVi, {
        validators: [Validators.maxLength(200)],
      }),
      openingHoursEn: new FormControl(siteSettingRawValue.openingHoursEn, {
        validators: [Validators.maxLength(200)],
      }),
      metaDescriptionVi: new FormControl(siteSettingRawValue.metaDescriptionVi, {
        validators: [Validators.maxLength(300)],
      }),
      metaDescriptionEn: new FormControl(siteSettingRawValue.metaDescriptionEn, {
        validators: [Validators.maxLength(300)],
      }),
      policyContentVi: new FormControl(siteSettingRawValue.policyContentVi),
      policyContentEn: new FormControl(siteSettingRawValue.policyContentEn),
    });
  }

  getSiteSetting(form: SiteSettingFormGroup): ISiteSetting | NewSiteSetting {
    return form.getRawValue() as ISiteSetting | NewSiteSetting;
  }

  resetForm(form: SiteSettingFormGroup, siteSetting: SiteSettingFormGroupInput): void {
    const siteSettingRawValue = { ...this.getFormDefaults(), ...siteSetting };
    form.reset(
      {
        ...siteSettingRawValue,
        id: { value: siteSettingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SiteSettingFormDefaults {
    return {
      id: null,
      heroVideoEnabled: false,
    };
  }
}
