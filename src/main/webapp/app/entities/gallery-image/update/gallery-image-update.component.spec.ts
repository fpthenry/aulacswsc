import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBranch } from 'app/entities/branch/branch.model';
import { BranchService } from 'app/entities/branch/service/branch.service';
import { GalleryImageService } from '../service/gallery-image.service';
import { IGalleryImage } from '../gallery-image.model';
import { GalleryImageFormService } from './gallery-image-form.service';

import { GalleryImageUpdateComponent } from './gallery-image-update.component';

describe('GalleryImage Management Update Component', () => {
  let comp: GalleryImageUpdateComponent;
  let fixture: ComponentFixture<GalleryImageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let galleryImageFormService: GalleryImageFormService;
  let galleryImageService: GalleryImageService;
  let branchService: BranchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GalleryImageUpdateComponent],
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
      .overrideTemplate(GalleryImageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GalleryImageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    galleryImageFormService = TestBed.inject(GalleryImageFormService);
    galleryImageService = TestBed.inject(GalleryImageService);
    branchService = TestBed.inject(BranchService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Branch query and add missing value', () => {
      const galleryImage: IGalleryImage = { id: 4556 };
      const branch: IBranch = { id: 23953 };
      galleryImage.branch = branch;

      const branchCollection: IBranch[] = [{ id: 23953 }];
      jest.spyOn(branchService, 'query').mockReturnValue(of(new HttpResponse({ body: branchCollection })));
      const additionalBranches = [branch];
      const expectedCollection: IBranch[] = [...additionalBranches, ...branchCollection];
      jest.spyOn(branchService, 'addBranchToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ galleryImage });
      comp.ngOnInit();

      expect(branchService.query).toHaveBeenCalled();
      expect(branchService.addBranchToCollectionIfMissing).toHaveBeenCalledWith(
        branchCollection,
        ...additionalBranches.map(expect.objectContaining),
      );
      expect(comp.branchesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const galleryImage: IGalleryImage = { id: 4556 };
      const branch: IBranch = { id: 23953 };
      galleryImage.branch = branch;

      activatedRoute.data = of({ galleryImage });
      comp.ngOnInit();

      expect(comp.branchesSharedCollection).toContainEqual(branch);
      expect(comp.galleryImage).toEqual(galleryImage);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGalleryImage>>();
      const galleryImage = { id: 21370 };
      jest.spyOn(galleryImageFormService, 'getGalleryImage').mockReturnValue(galleryImage);
      jest.spyOn(galleryImageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ galleryImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: galleryImage }));
      saveSubject.complete();

      // THEN
      expect(galleryImageFormService.getGalleryImage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(galleryImageService.update).toHaveBeenCalledWith(expect.objectContaining(galleryImage));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGalleryImage>>();
      const galleryImage = { id: 21370 };
      jest.spyOn(galleryImageFormService, 'getGalleryImage').mockReturnValue({ id: null });
      jest.spyOn(galleryImageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ galleryImage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: galleryImage }));
      saveSubject.complete();

      // THEN
      expect(galleryImageFormService.getGalleryImage).toHaveBeenCalled();
      expect(galleryImageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGalleryImage>>();
      const galleryImage = { id: 21370 };
      jest.spyOn(galleryImageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ galleryImage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(galleryImageService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBranch', () => {
      it('should forward to branchService', () => {
        const entity = { id: 23953 };
        const entity2 = { id: 1770 };
        jest.spyOn(branchService, 'compareBranch');
        comp.compareBranch(entity, entity2);
        expect(branchService.compareBranch).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
