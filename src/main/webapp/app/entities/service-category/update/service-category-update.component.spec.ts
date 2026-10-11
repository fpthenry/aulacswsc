import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ServiceCategoryService } from '../service/service-category.service';
import { IServiceCategory } from '../service-category.model';
import { ServiceCategoryFormService } from './service-category-form.service';

import { ServiceCategoryUpdateComponent } from './service-category-update.component';

describe('ServiceCategory Management Update Component', () => {
  let comp: ServiceCategoryUpdateComponent;
  let fixture: ComponentFixture<ServiceCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let serviceCategoryFormService: ServiceCategoryFormService;
  let serviceCategoryService: ServiceCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ServiceCategoryUpdateComponent],
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
      .overrideTemplate(ServiceCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServiceCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    serviceCategoryFormService = TestBed.inject(ServiceCategoryFormService);
    serviceCategoryService = TestBed.inject(ServiceCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const serviceCategory: IServiceCategory = { id: 22395 };

      activatedRoute.data = of({ serviceCategory });
      comp.ngOnInit();

      expect(comp.serviceCategory).toEqual(serviceCategory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceCategory>>();
      const serviceCategory = { id: 13984 };
      jest.spyOn(serviceCategoryFormService, 'getServiceCategory').mockReturnValue(serviceCategory);
      jest.spyOn(serviceCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serviceCategory }));
      saveSubject.complete();

      // THEN
      expect(serviceCategoryFormService.getServiceCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(serviceCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(serviceCategory));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceCategory>>();
      const serviceCategory = { id: 13984 };
      jest.spyOn(serviceCategoryFormService, 'getServiceCategory').mockReturnValue({ id: null });
      jest.spyOn(serviceCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serviceCategory }));
      saveSubject.complete();

      // THEN
      expect(serviceCategoryFormService.getServiceCategory).toHaveBeenCalled();
      expect(serviceCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServiceCategory>>();
      const serviceCategory = { id: 13984 };
      jest.spyOn(serviceCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serviceCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(serviceCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
