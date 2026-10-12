import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { SiteSettingService } from '../service/site-setting.service';
import { ISiteSetting } from '../site-setting.model';
import { SiteSettingFormService } from './site-setting-form.service';

import { SiteSettingUpdateComponent } from './site-setting-update.component';

describe('SiteSetting Management Update Component', () => {
  let comp: SiteSettingUpdateComponent;
  let fixture: ComponentFixture<SiteSettingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let siteSettingFormService: SiteSettingFormService;
  let siteSettingService: SiteSettingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SiteSettingUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SiteSettingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SiteSettingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    siteSettingFormService = TestBed.inject(SiteSettingFormService);
    siteSettingService = TestBed.inject(SiteSettingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const siteSetting: ISiteSetting = { id: 12335 };

      activatedRoute.data = of({ siteSetting });
      comp.ngOnInit();

      expect(comp.siteSetting).toEqual(siteSetting);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISiteSetting>>();
      const siteSetting = { id: 19433 };
      jest.spyOn(siteSettingFormService, 'getSiteSetting').mockReturnValue(siteSetting);
      jest.spyOn(siteSettingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ siteSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: siteSetting }));
      saveSubject.complete();

      // THEN
      expect(siteSettingFormService.getSiteSetting).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(siteSettingService.update).toHaveBeenCalledWith(expect.objectContaining(siteSetting));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISiteSetting>>();
      const siteSetting = { id: 19433 };
      jest.spyOn(siteSettingFormService, 'getSiteSetting').mockReturnValue({ id: null });
      jest.spyOn(siteSettingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ siteSetting: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: siteSetting }));
      saveSubject.complete();

      // THEN
      expect(siteSettingFormService.getSiteSetting).toHaveBeenCalled();
      expect(siteSettingService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISiteSetting>>();
      const siteSetting = { id: 19433 };
      jest.spyOn(siteSettingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ siteSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(siteSettingService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
