import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { BranchService } from '../service/branch.service';
import { IBranch } from '../branch.model';
import { BranchFormService } from './branch-form.service';

import { BranchUpdateComponent } from './branch-update.component';

describe('Branch Management Update Component', () => {
  let comp: BranchUpdateComponent;
  let fixture: ComponentFixture<BranchUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let branchFormService: BranchFormService;
  let branchService: BranchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BranchUpdateComponent],
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
      .overrideTemplate(BranchUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BranchUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    branchFormService = TestBed.inject(BranchFormService);
    branchService = TestBed.inject(BranchService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const branch: IBranch = { id: 1770 };

      activatedRoute.data = of({ branch });
      comp.ngOnInit();

      expect(comp.branch).toEqual(branch);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBranch>>();
      const branch = { id: 23953 };
      jest.spyOn(branchFormService, 'getBranch').mockReturnValue(branch);
      jest.spyOn(branchService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ branch });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: branch }));
      saveSubject.complete();

      // THEN
      expect(branchFormService.getBranch).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(branchService.update).toHaveBeenCalledWith(expect.objectContaining(branch));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBranch>>();
      const branch = { id: 23953 };
      jest.spyOn(branchFormService, 'getBranch').mockReturnValue({ id: null });
      jest.spyOn(branchService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ branch: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: branch }));
      saveSubject.complete();

      // THEN
      expect(branchFormService.getBranch).toHaveBeenCalled();
      expect(branchService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBranch>>();
      const branch = { id: 23953 };
      jest.spyOn(branchService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ branch });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(branchService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
