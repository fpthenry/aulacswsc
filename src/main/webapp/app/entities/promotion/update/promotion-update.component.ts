import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';
import { PromotionFormGroup, PromotionFormService } from './promotion-form.service';

@Component({
  selector: 'jhi-promotion-update',
  templateUrl: './promotion-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PromotionUpdateComponent implements OnInit {
  isSaving = false;
  promotion: IPromotion | null = null;

  protected promotionService = inject(PromotionService);
  protected promotionFormService = inject(PromotionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PromotionFormGroup = this.promotionFormService.createPromotionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ promotion }) => {
      this.promotion = promotion;
      if (promotion) {
        this.updateForm(promotion);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const promotion = this.promotionFormService.getPromotion(this.editForm);
    if (promotion.id !== null) {
      this.subscribeToSaveResponse(this.promotionService.update(promotion));
    } else {
      this.subscribeToSaveResponse(this.promotionService.create(promotion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPromotion>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(promotion: IPromotion): void {
    this.promotion = promotion;
    this.promotionFormService.resetForm(this.editForm, promotion);
  }
}
