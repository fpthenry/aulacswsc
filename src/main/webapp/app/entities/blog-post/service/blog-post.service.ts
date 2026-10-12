import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBlogPost, NewBlogPost } from '../blog-post.model';

export type PartialUpdateBlogPost = Partial<IBlogPost> & Pick<IBlogPost, 'id'>;

type RestOf<T extends IBlogPost | NewBlogPost> = Omit<T, 'publishedAt'> & {
  publishedAt?: string | null;
};

export type RestBlogPost = RestOf<IBlogPost>;

export type NewRestBlogPost = RestOf<NewBlogPost>;

export type PartialUpdateRestBlogPost = RestOf<PartialUpdateBlogPost>;

export type EntityResponseType = HttpResponse<IBlogPost>;
export type EntityArrayResponseType = HttpResponse<IBlogPost[]>;

@Injectable({ providedIn: 'root' })
export class BlogPostService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/blog-posts');

  create(blogPost: NewBlogPost): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(blogPost);
    return this.http
      .post<RestBlogPost>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(blogPost: IBlogPost): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(blogPost);
    return this.http
      .put<RestBlogPost>(`${this.resourceUrl}/${this.getBlogPostIdentifier(blogPost)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(blogPost: PartialUpdateBlogPost): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(blogPost);
    return this.http
      .patch<RestBlogPost>(`${this.resourceUrl}/${this.getBlogPostIdentifier(blogPost)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBlogPost>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBlogPost[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBlogPostIdentifier(blogPost: Pick<IBlogPost, 'id'>): number {
    return blogPost.id;
  }

  compareBlogPost(o1: Pick<IBlogPost, 'id'> | null, o2: Pick<IBlogPost, 'id'> | null): boolean {
    return o1 && o2 ? this.getBlogPostIdentifier(o1) === this.getBlogPostIdentifier(o2) : o1 === o2;
  }

  addBlogPostToCollectionIfMissing<Type extends Pick<IBlogPost, 'id'>>(
    blogPostCollection: Type[],
    ...blogPostsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const blogPosts: Type[] = blogPostsToCheck.filter(isPresent);
    if (blogPosts.length > 0) {
      const blogPostCollectionIdentifiers = blogPostCollection.map(blogPostItem => this.getBlogPostIdentifier(blogPostItem));
      const blogPostsToAdd = blogPosts.filter(blogPostItem => {
        const blogPostIdentifier = this.getBlogPostIdentifier(blogPostItem);
        if (blogPostCollectionIdentifiers.includes(blogPostIdentifier)) {
          return false;
        }
        blogPostCollectionIdentifiers.push(blogPostIdentifier);
        return true;
      });
      return [...blogPostsToAdd, ...blogPostCollection];
    }
    return blogPostCollection;
  }

  protected convertDateFromClient<T extends IBlogPost | NewBlogPost | PartialUpdateBlogPost>(blogPost: T): RestOf<T> {
    return {
      ...blogPost,
      publishedAt: blogPost.publishedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restBlogPost: RestBlogPost): IBlogPost {
    return {
      ...restBlogPost,
      publishedAt: restBlogPost.publishedAt ? dayjs(restBlogPost.publishedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBlogPost>): HttpResponse<IBlogPost> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBlogPost[]>): HttpResponse<IBlogPost[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
