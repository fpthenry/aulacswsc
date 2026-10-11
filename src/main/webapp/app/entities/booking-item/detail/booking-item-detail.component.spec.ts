import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookingItemDetailComponent } from './booking-item-detail.component';

describe('BookingItem Management Detail Component', () => {
  let comp: BookingItemDetailComponent;
  let fixture: ComponentFixture<BookingItemDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingItemDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./booking-item-detail.component').then(m => m.BookingItemDetailComponent),
              resolve: { bookingItem: () => of({ id: 7381 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookingItemDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookingItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load bookingItem on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookingItemDetailComponent);

      // THEN
      expect(instance.bookingItem()).toEqual(expect.objectContaining({ id: 7381 }));
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
