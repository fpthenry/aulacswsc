import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPromotion, NewPromotion } from '../promotion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPromotion for edit and NewPromotionFormGroupInput for create.
 */
type PromotionFormGroupInput = IPromotion | PartialWithRequiredKeyOf<NewPromotion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPromotion | NewPromotion> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type PromotionFormRawValue = FormValueOf<IPromotion>;

type NewPromotionFormRawValue = FormValueOf<NewPromotion>;

type PromotionFormDefaults = Pick<NewPromotion, 'id' | 'startDate' | 'endDate' | 'active'>;

type PromotionFormGroupContent = {
  id: FormControl<PromotionFormRawValue['id'] | NewPromotion['id']>;
  code: FormControl<PromotionFormRawValue['code']>;
  name: FormControl<PromotionFormRawValue['name']>;
  description: FormControl<PromotionFormRawValue['description']>;
  discountPercent: FormControl<PromotionFormRawValue['discountPercent']>;
  discountAmount: FormControl<PromotionFormRawValue['discountAmount']>;
  startDate: FormControl<PromotionFormRawValue['startDate']>;
  endDate: FormControl<PromotionFormRawValue['endDate']>;
  active: FormControl<PromotionFormRawValue['active']>;
};

export type PromotionFormGroup = FormGroup<PromotionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PromotionFormService {
  createPromotionFormGroup(promotion: PromotionFormGroupInput = { id: null }): PromotionFormGroup {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({
      ...this.getFormDefaults(),
      ...promotion,
    });
    return new FormGroup<PromotionFormGroupContent>({
      id: new FormControl(
        { value: promotionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(promotionRawValue.code, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      name: new FormControl(promotionRawValue.name, {
        validators: [Validators.required, Validators.maxLength(200)],
      }),
      description: new FormControl(promotionRawValue.description, {
        validators: [Validators.maxLength(1000)],
      }),
      discountPercent: new FormControl(promotionRawValue.discountPercent, {
        validators: [Validators.min(0), Validators.max(100)],
      }),
      discountAmount: new FormControl(promotionRawValue.discountAmount, {
        validators: [Validators.min(0)],
      }),
      startDate: new FormControl(promotionRawValue.startDate),
      endDate: new FormControl(promotionRawValue.endDate),
      active: new FormControl(promotionRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getPromotion(form: PromotionFormGroup): IPromotion | NewPromotion {
    return this.convertPromotionRawValueToPromotion(form.getRawValue() as PromotionFormRawValue | NewPromotionFormRawValue);
  }

  resetForm(form: PromotionFormGroup, promotion: PromotionFormGroupInput): void {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({ ...this.getFormDefaults(), ...promotion });
    form.reset(
      {
        ...promotionRawValue,
        id: { value: promotionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PromotionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
      active: false,
    };
  }

  private convertPromotionRawValueToPromotion(rawPromotion: PromotionFormRawValue | NewPromotionFormRawValue): IPromotion | NewPromotion {
    return {
      ...rawPromotion,
      startDate: dayjs(rawPromotion.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawPromotion.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertPromotionToPromotionRawValue(
    promotion: IPromotion | (Partial<NewPromotion> & PromotionFormDefaults),
  ): PromotionFormRawValue | PartialWithRequiredKeyOf<NewPromotionFormRawValue> {
    return {
      ...promotion,
      startDate: promotion.startDate ? promotion.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: promotion.endDate ? promotion.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
