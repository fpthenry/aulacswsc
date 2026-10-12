import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { GalleryImageDetailComponent } from './gallery-image-detail.component';

describe('GalleryImage Management Detail Component', () => {
  let comp: GalleryImageDetailComponent;
  let fixture: ComponentFixture<GalleryImageDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GalleryImageDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./gallery-image-detail.component').then(m => m.GalleryImageDetailComponent),
              resolve: { galleryImage: () => of({ id: 21370 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(GalleryImageDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GalleryImageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load galleryImage on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', GalleryImageDetailComponent);

      // THEN
      expect(instance.galleryImage()).toEqual(expect.objectContaining({ id: 21370 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
