import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BranchDetailComponent } from './branch-detail.component';

describe('Branch Management Detail Component', () => {
  let comp: BranchDetailComponent;
  let fixture: ComponentFixture<BranchDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BranchDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./branch-detail.component').then(m => m.BranchDetailComponent),
              resolve: { branch: () => of({ id: 23953 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BranchDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load branch on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BranchDetailComponent);

      // THEN
      expect(instance.branch()).toEqual(expect.objectContaining({ id: 23953 }));
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
