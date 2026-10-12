import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { ImageUploadComponent } from 'app/shared/image-upload/image-upload.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { BlogCategory } from 'app/entities/enumerations/blog-category.model';
import { BlogPostService } from '../service/blog-post.service';
import { IBlogPost } from '../blog-post.model';
import { BlogPostFormGroup, BlogPostFormService } from './blog-post-form.service';

@Component({
  selector: 'jhi-blog-post-update',
  templateUrl: './blog-post-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, ImageUploadComponent],
})
export class BlogPostUpdateComponent implements OnInit {
  isSaving = false;
  blogPost: IBlogPost | null = null;
  blogCategoryValues = Object.keys(BlogCategory);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected blogPostService = inject(BlogPostService);
  protected blogPostFormService = inject(BlogPostFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BlogPostFormGroup = this.blogPostFormService.createBlogPostFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ blogPost }) => {
      this.blogPost = blogPost;
      if (blogPost) {
        this.updateForm(blogPost);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('aulacswscApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const blogPost = this.blogPostFormService.getBlogPost(this.editForm);
    if (blogPost.id !== null) {
      this.subscribeToSaveResponse(this.blogPostService.update(blogPost));
    } else {
      this.subscribeToSaveResponse(this.blogPostService.create(blogPost));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBlogPost>>): void {
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

  protected updateForm(blogPost: IBlogPost): void {
    this.blogPost = blogPost;
    this.blogPostFormService.resetForm(this.editForm, blogPost);
  }
}
