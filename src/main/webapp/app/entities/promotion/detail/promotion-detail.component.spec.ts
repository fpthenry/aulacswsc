import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PromotionDetailComponent } from './promotion-detail.component';

describe('Promotion Management Detail Component', () => {
  let comp: PromotionDetailComponent;
  let fixture: ComponentFixture<PromotionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PromotionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./promotion-detail.component').then(m => m.PromotionDetailComponent),
              resolve: { promotion: () => of({ id: 30739 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PromotionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PromotionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load promotion on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PromotionDetailComponent);

      // THEN
      expect(instance.promotion()).toEqual(expect.objectContaining({ id: 30739 }));
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
