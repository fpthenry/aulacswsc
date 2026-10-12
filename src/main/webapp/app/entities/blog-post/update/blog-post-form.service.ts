import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBlogPost, NewBlogPost } from '../blog-post.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBlogPost for edit and NewBlogPostFormGroupInput for create.
 */
type BlogPostFormGroupInput = IBlogPost | PartialWithRequiredKeyOf<NewBlogPost>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBlogPost | NewBlogPost> = Omit<T, 'publishedAt'> & {
  publishedAt?: string | null;
};

type BlogPostFormRawValue = FormValueOf<IBlogPost>;

type NewBlogPostFormRawValue = FormValueOf<NewBlogPost>;

type BlogPostFormDefaults = Pick<NewBlogPost, 'id' | 'published' | 'publishedAt'>;

type BlogPostFormGroupContent = {
  id: FormControl<BlogPostFormRawValue['id'] | NewBlogPost['id']>;
  titleVi: FormControl<BlogPostFormRawValue['titleVi']>;
  titleEn: FormControl<BlogPostFormRawValue['titleEn']>;
  slug: FormControl<BlogPostFormRawValue['slug']>;
  excerptVi: FormControl<BlogPostFormRawValue['excerptVi']>;
  excerptEn: FormControl<BlogPostFormRawValue['excerptEn']>;
  contentVi: FormControl<BlogPostFormRawValue['contentVi']>;
  contentEn: FormControl<BlogPostFormRawValue['contentEn']>;
  coverImageUrl: FormControl<BlogPostFormRawValue['coverImageUrl']>;
  category: FormControl<BlogPostFormRawValue['category']>;
  published: FormControl<BlogPostFormRawValue['published']>;
  publishedAt: FormControl<BlogPostFormRawValue['publishedAt']>;
};

export type BlogPostFormGroup = FormGroup<BlogPostFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BlogPostFormService {
  createBlogPostFormGroup(blogPost: BlogPostFormGroupInput = { id: null }): BlogPostFormGroup {
    const blogPostRawValue = this.convertBlogPostToBlogPostRawValue({
      ...this.getFormDefaults(),
      ...blogPost,
    });
    return new FormGroup<BlogPostFormGroupContent>({
      id: new FormControl(
        { value: blogPostRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleVi: new FormControl(blogPostRawValue.titleVi, {
        validators: [Validators.required, Validators.maxLength(250)],
      }),
      titleEn: new FormControl(blogPostRawValue.titleEn, {
        validators: [Validators.maxLength(250)],
      }),
      slug: new FormControl(blogPostRawValue.slug, {
        validators: [Validators.maxLength(280)],
      }),
      excerptVi: new FormControl(blogPostRawValue.excerptVi, {
        validators: [Validators.maxLength(500)],
      }),
      excerptEn: new FormControl(blogPostRawValue.excerptEn, {
        validators: [Validators.maxLength(500)],
      }),
      contentVi: new FormControl(blogPostRawValue.contentVi),
      contentEn: new FormControl(blogPostRawValue.contentEn),
      coverImageUrl: new FormControl(blogPostRawValue.coverImageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      category: new FormControl(blogPostRawValue.category),
      published: new FormControl(blogPostRawValue.published, {
        validators: [Validators.required],
      }),
      publishedAt: new FormControl(blogPostRawValue.publishedAt),
    });
  }

  getBlogPost(form: BlogPostFormGroup): IBlogPost | NewBlogPost {
    return this.convertBlogPostRawValueToBlogPost(form.getRawValue() as BlogPostFormRawValue | NewBlogPostFormRawValue);
  }

  resetForm(form: BlogPostFormGroup, blogPost: BlogPostFormGroupInput): void {
    const blogPostRawValue = this.convertBlogPostToBlogPostRawValue({ ...this.getFormDefaults(), ...blogPost });
    form.reset(
      {
        ...blogPostRawValue,
        id: { value: blogPostRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BlogPostFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      published: false,
      publishedAt: currentTime,
    };
  }

  private convertBlogPostRawValueToBlogPost(rawBlogPost: BlogPostFormRawValue | NewBlogPostFormRawValue): IBlogPost | NewBlogPost {
    return {
      ...rawBlogPost,
      publishedAt: dayjs(rawBlogPost.publishedAt, DATE_TIME_FORMAT),
    };
  }

  private convertBlogPostToBlogPostRawValue(
    blogPost: IBlogPost | (Partial<NewBlogPost> & BlogPostFormDefaults),
  ): BlogPostFormRawValue | PartialWithRequiredKeyOf<NewBlogPostFormRawValue> {
    return {
      ...blogPost,
      publishedAt: blogPost.publishedAt ? blogPost.publishedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
