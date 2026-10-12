import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IBranch } from 'app/entities/branch/branch.model';
import { BranchService } from 'app/entities/branch/service/branch.service';
import { ISpaService } from 'app/entities/spa-service/spa-service.model';
import { SpaServiceService } from 'app/entities/spa-service/service/spa-service.service';
import { IStaff } from '../staff.model';
import { StaffService } from '../service/staff.service';
import { StaffFormService } from './staff-form.service';

import { StaffUpdateComponent } from './staff-update.component';

describe('Staff Management Update Component', () => {
  let comp: StaffUpdateComponent;
  let fixture: ComponentFixture<StaffUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let staffFormService: StaffFormService;
  let staffService: StaffService;
  let userService: UserService;
  let branchService: BranchService;
  let spaServiceService: SpaServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StaffUpdateComponent],
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
      .overrideTemplate(StaffUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StaffUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    staffFormService = TestBed.inject(StaffFormService);
    staffService = TestBed.inject(StaffService);
    userService = TestBed.inject(UserService);
    branchService = TestBed.inject(BranchService);
    spaServiceService = TestBed.inject(SpaServiceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const staff: IStaff = { id: 8575 };
      const user: IUser = { id: 3944 };
      staff.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Branch query and add missing value', () => {
      const staff: IStaff = { id: 8575 };
      const branch: IBranch = { id: 23953 };
      staff.branch = branch;

      const branchCollection: IBranch[] = [{ id: 23953 }];
      jest.spyOn(branchService, 'query').mockReturnValue(of(new HttpResponse({ body: branchCollection })));
      const additionalBranches = [branch];
      const expectedCollection: IBranch[] = [...additionalBranches, ...branchCollection];
      jest.spyOn(branchService, 'addBranchToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(branchService.query).toHaveBeenCalled();
      expect(branchService.addBranchToCollectionIfMissing).toHaveBeenCalledWith(
        branchCollection,
        ...additionalBranches.map(expect.objectContaining),
      );
      expect(comp.branchesSharedCollection).toEqual(expectedCollection);
    });

    it('should call SpaService query and add missing value', () => {
      const staff: IStaff = { id: 8575 };
      const services: ISpaService[] = [{ id: 19409 }];
      staff.services = services;

      const spaServiceCollection: ISpaService[] = [{ id: 19409 }];
      jest.spyOn(spaServiceService, 'query').mockReturnValue(of(new HttpResponse({ body: spaServiceCollection })));
      const additionalSpaServices = [...services];
      const expectedCollection: ISpaService[] = [...additionalSpaServices, ...spaServiceCollection];
      jest.spyOn(spaServiceService, 'addSpaServiceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(spaServiceService.query).toHaveBeenCalled();
      expect(spaServiceService.addSpaServiceToCollectionIfMissing).toHaveBeenCalledWith(
        spaServiceCollection,
        ...additionalSpaServices.map(expect.objectContaining),
      );
      expect(comp.spaServicesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const staff: IStaff = { id: 8575 };
      const user: IUser = { id: 3944 };
      staff.user = user;
      const branch: IBranch = { id: 23953 };
      staff.branch = branch;
      const services: ISpaService = { id: 19409 };
      staff.services = [services];

      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.branchesSharedCollection).toContainEqual(branch);
      expect(comp.spaServicesSharedCollection).toContainEqual(services);
      expect(comp.staff).toEqual(staff);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 10841 };
      jest.spyOn(staffFormService, 'getStaff').mockReturnValue(staff);
      jest.spyOn(staffService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: staff }));
      saveSubject.complete();

      // THEN
      expect(staffFormService.getStaff).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(staffService.update).toHaveBeenCalledWith(expect.objectContaining(staff));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 10841 };
      jest.spyOn(staffFormService, 'getStaff').mockReturnValue({ id: null });
      jest.spyOn(staffService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: staff }));
      saveSubject.complete();

      // THEN
      expect(staffFormService.getStaff).toHaveBeenCalled();
      expect(staffService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStaff>>();
      const staff = { id: 10841 };
      jest.spyOn(staffService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ staff });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(staffService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBranch', () => {
      it('should forward to branchService', () => {
        const entity = { id: 23953 };
        const entity2 = { id: 1770 };
        jest.spyOn(branchService, 'compareBranch');
        comp.compareBranch(entity, entity2);
        expect(branchService.compareBranch).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSpaService', () => {
      it('should forward to spaServiceService', () => {
        const entity = { id: 19409 };
        const entity2 = { id: 13678 };
        jest.spyOn(spaServiceService, 'compareSpaService');
        comp.compareSpaService(entity, entity2);
        expect(spaServiceService.compareSpaService).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
