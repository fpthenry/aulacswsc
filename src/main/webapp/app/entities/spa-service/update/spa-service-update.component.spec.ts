import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IServiceCategory } from 'app/entities/service-category/service-category.model';
import { ServiceCategoryService } from 'app/entities/service-category/service/service-category.service';
import { IStaff } from 'app/entities/staff/staff.model';
import { StaffService } from 'app/entities/staff/service/staff.service';
import { ISpaService } from '../spa-service.model';
import { SpaServiceService } from '../service/spa-service.service';
import { SpaServiceFormService } from './spa-service-form.service';

import { SpaServiceUpdateComponent } from './spa-service-update.component';

describe('SpaService Management Update Component', () => {
  let comp: SpaServiceUpdateComponent;
  let fixture: ComponentFixture<SpaServiceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spaServiceFormService: SpaServiceFormService;
  let spaServiceService: SpaServiceService;
  let serviceCategoryService: ServiceCategoryService;
  let staffService: StaffService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SpaServiceUpdateComponent],
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
      .overrideTemplate(SpaServiceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpaServiceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spaServiceFormService = TestBed.inject(SpaServiceFormService);
    spaServiceService = TestBed.inject(SpaServiceService);
    serviceCategoryService = TestBed.inject(ServiceCategoryService);
    staffService = TestBed.inject(StaffService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ServiceCategory query and add missing value', () => {
      const spaService: ISpaService = { id: 13678 };
      const category: IServiceCategory = { id: 13984 };
      spaService.category = category;

      const serviceCategoryCollection: IServiceCategory[] = [{ id: 13984 }];
      jest.spyOn(serviceCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: serviceCategoryCollection })));
      const additionalServiceCategories = [category];
      const expectedCollection: IServiceCategory[] = [...additionalServiceCategories, ...serviceCategoryCollection];
      jest.spyOn(serviceCategoryService, 'addServiceCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ spaService });
      comp.ngOnInit();

      expect(serviceCategoryService.query).toHaveBeenCalled();
      expect(serviceCategoryService.addServiceCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        serviceCategoryCollection,
        ...additionalServiceCategories.map(expect.objectContaining),
      );
      expect(comp.serviceCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Staff query and add missing value', () => {
      const spaService: ISpaService = { id: 13678 };
      const staffs: IStaff[] = [{ id: 10841 }];
      spaService.staffs = staffs;

      const staffCollection: IStaff[] = [{ id: 10841 }];
      jest.spyOn(staffService, 'query').mockReturnValue(of(new HttpResponse({ body: staffCollection })));
      const additionalStaff = [...staffs];
      const expectedCollection: IStaff[] = [...additionalStaff, ...staffCollection];
      jest.spyOn(staffService, 'addStaffToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ spaService });
      comp.ngOnInit();

      expect(staffService.query).toHaveBeenCalled();
      expect(staffService.addStaffToCollectionIfMissing).toHaveBeenCalledWith(
        staffCollection,
        ...additionalStaff.map(expect.objectContaining),
      );
      expect(comp.staffSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const spaService: ISpaService = { id: 13678 };
      const category: IServiceCategory = { id: 13984 };
      spaService.category = category;
      const staffs: IStaff = { id: 10841 };
      spaService.staffs = [staffs];

      activatedRoute.data = of({ spaService });
      comp.ngOnInit();

      expect(comp.serviceCategoriesSharedCollection).toContainEqual(category);
      expect(comp.staffSharedCollection).toContainEqual(staffs);
      expect(comp.spaService).toEqual(spaService);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaService>>();
      const spaService = { id: 19409 };
      jest.spyOn(spaServiceFormService, 'getSpaService').mockReturnValue(spaService);
      jest.spyOn(spaServiceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaService });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaService }));
      saveSubject.complete();

      // THEN
      expect(spaServiceFormService.getSpaService).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(spaServiceService.update).toHaveBeenCalledWith(expect.objectContaining(spaService));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaService>>();
      const spaService = { id: 19409 };
      jest.spyOn(spaServiceFormService, 'getSpaService').mockReturnValue({ id: null });
      jest.spyOn(spaServiceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaService: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaService }));
      saveSubject.complete();

      // THEN
      expect(spaServiceFormService.getSpaService).toHaveBeenCalled();
      expect(spaServiceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpaService>>();
      const spaService = { id: 19409 };
      jest.spyOn(spaServiceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaService });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spaServiceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareServiceCategory', () => {
      it('should forward to serviceCategoryService', () => {
        const entity = { id: 13984 };
        const entity2 = { id: 22395 };
        jest.spyOn(serviceCategoryService, 'compareServiceCategory');
        comp.compareServiceCategory(entity, entity2);
        expect(serviceCategoryService.compareServiceCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStaff', () => {
      it('should forward to staffService', () => {
        const entity = { id: 10841 };
        const entity2 = { id: 8575 };
        jest.spyOn(staffService, 'compareStaff');
        comp.compareStaff(entity, entity2);
        expect(staffService.compareStaff).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
