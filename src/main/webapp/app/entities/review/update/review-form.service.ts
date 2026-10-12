import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReview, NewReview } from '../review.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReview for edit and NewReviewFormGroupInput for create.
 */
type ReviewFormGroupInput = IReview | PartialWithRequiredKeyOf<NewReview>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReview | NewReview> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type ReviewFormRawValue = FormValueOf<IReview>;

type NewReviewFormRawValue = FormValueOf<NewReview>;

type ReviewFormDefaults = Pick<NewReview, 'id' | 'approved' | 'featured' | 'createdDate'>;

type ReviewFormGroupContent = {
  id: FormControl<ReviewFormRawValue['id'] | NewReview['id']>;
  authorName: FormControl<ReviewFormRawValue['authorName']>;
  rating: FormControl<ReviewFormRawValue['rating']>;
  comment: FormControl<ReviewFormRawValue['comment']>;
  videoUrl: FormControl<ReviewFormRawValue['videoUrl']>;
  reviewDate: FormControl<ReviewFormRawValue['reviewDate']>;
  approved: FormControl<ReviewFormRawValue['approved']>;
  featured: FormControl<ReviewFormRawValue['featured']>;
  createdDate: FormControl<ReviewFormRawValue['createdDate']>;
  customer: FormControl<ReviewFormRawValue['customer']>;
  service: FormControl<ReviewFormRawValue['service']>;
};

export type ReviewFormGroup = FormGroup<ReviewFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReviewFormService {
  createReviewFormGroup(review: ReviewFormGroupInput = { id: null }): ReviewFormGroup {
    const reviewRawValue = this.convertReviewToReviewRawValue({
      ...this.getFormDefaults(),
      ...review,
    });
    return new FormGroup<ReviewFormGroupContent>({
      id: new FormControl(
        { value: reviewRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      authorName: new FormControl(reviewRawValue.authorName, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      rating: new FormControl(reviewRawValue.rating, {
        validators: [Validators.required, Validators.min(1), Validators.max(5)],
      }),
      comment: new FormControl(reviewRawValue.comment, {
        validators: [Validators.maxLength(2000)],
      }),
      videoUrl: new FormControl(reviewRawValue.videoUrl, {
        validators: [Validators.maxLength(500)],
      }),
      reviewDate: new FormControl(reviewRawValue.reviewDate),
      approved: new FormControl(reviewRawValue.approved),
      featured: new FormControl(reviewRawValue.featured),
      createdDate: new FormControl(reviewRawValue.createdDate),
      customer: new FormControl(reviewRawValue.customer),
      service: new FormControl(reviewRawValue.service),
    });
  }

  getReview(form: ReviewFormGroup): IReview | NewReview {
    return this.convertReviewRawValueToReview(form.getRawValue() as ReviewFormRawValue | NewReviewFormRawValue);
  }

  resetForm(form: ReviewFormGroup, review: ReviewFormGroupInput): void {
    const reviewRawValue = this.convertReviewToReviewRawValue({ ...this.getFormDefaults(), ...review });
    form.reset(
      {
        ...reviewRawValue,
        id: { value: reviewRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReviewFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      approved: false,
      featured: false,
      createdDate: currentTime,
    };
  }

  private convertReviewRawValueToReview(rawReview: ReviewFormRawValue | NewReviewFormRawValue): IReview | NewReview {
    return {
      ...rawReview,
      createdDate: dayjs(rawReview.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertReviewToReviewRawValue(
    review: IReview | (Partial<NewReview> & ReviewFormDefaults),
  ): ReviewFormRawValue | PartialWithRequiredKeyOf<NewReviewFormRawValue> {
    return {
      ...review,
      createdDate: review.createdDate ? review.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
