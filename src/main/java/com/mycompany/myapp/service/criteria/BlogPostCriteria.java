package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.BlogCategory;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.BlogPost} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.BlogPostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /blog-posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPostCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BlogCategory
     */
    public static class BlogCategoryFilter extends Filter<BlogCategory> {

        public BlogCategoryFilter() {}

        public BlogCategoryFilter(BlogCategoryFilter filter) {
            super(filter);
        }

        @Override
        public BlogCategoryFilter copy() {
            return new BlogCategoryFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titleVi;

    private StringFilter titleEn;

    private StringFilter slug;

    private StringFilter excerptVi;

    private StringFilter excerptEn;

    private StringFilter coverImageUrl;

    private BlogCategoryFilter category;

    private BooleanFilter published;

    private InstantFilter publishedAt;

    private Boolean distinct;

    public BlogPostCriteria() {}

    public BlogPostCriteria(BlogPostCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titleVi = other.optionalTitleVi().map(StringFilter::copy).orElse(null);
        this.titleEn = other.optionalTitleEn().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.excerptVi = other.optionalExcerptVi().map(StringFilter::copy).orElse(null);
        this.excerptEn = other.optionalExcerptEn().map(StringFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(BlogCategoryFilter::copy).orElse(null);
        this.published = other.optionalPublished().map(BooleanFilter::copy).orElse(null);
        this.publishedAt = other.optionalPublishedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BlogPostCriteria copy() {
        return new BlogPostCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitleVi() {
        return titleVi;
    }

    public Optional<StringFilter> optionalTitleVi() {
        return Optional.ofNullable(titleVi);
    }

    public StringFilter titleVi() {
        if (titleVi == null) {
            setTitleVi(new StringFilter());
        }
        return titleVi;
    }

    public void setTitleVi(StringFilter titleVi) {
        this.titleVi = titleVi;
    }

    public StringFilter getTitleEn() {
        return titleEn;
    }

    public Optional<StringFilter> optionalTitleEn() {
        return Optional.ofNullable(titleEn);
    }

    public StringFilter titleEn() {
        if (titleEn == null) {
            setTitleEn(new StringFilter());
        }
        return titleEn;
    }

    public void setTitleEn(StringFilter titleEn) {
        this.titleEn = titleEn;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public StringFilter getExcerptVi() {
        return excerptVi;
    }

    public Optional<StringFilter> optionalExcerptVi() {
        return Optional.ofNullable(excerptVi);
    }

    public StringFilter excerptVi() {
        if (excerptVi == null) {
            setExcerptVi(new StringFilter());
        }
        return excerptVi;
    }

    public void setExcerptVi(StringFilter excerptVi) {
        this.excerptVi = excerptVi;
    }

    public StringFilter getExcerptEn() {
        return excerptEn;
    }

    public Optional<StringFilter> optionalExcerptEn() {
        return Optional.ofNullable(excerptEn);
    }

    public StringFilter excerptEn() {
        if (excerptEn == null) {
            setExcerptEn(new StringFilter());
        }
        return excerptEn;
    }

    public void setExcerptEn(StringFilter excerptEn) {
        this.excerptEn = excerptEn;
    }

    public StringFilter getCoverImageUrl() {
        return coverImageUrl;
    }

    public Optional<StringFilter> optionalCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public StringFilter coverImageUrl() {
        if (coverImageUrl == null) {
            setCoverImageUrl(new StringFilter());
        }
        return coverImageUrl;
    }

    public void setCoverImageUrl(StringFilter coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public BlogCategoryFilter getCategory() {
        return category;
    }

    public Optional<BlogCategoryFilter> optionalCategory() {
        return Optional.ofNullable(category);
    }

    public BlogCategoryFilter category() {
        if (category == null) {
            setCategory(new BlogCategoryFilter());
        }
        return category;
    }

    public void setCategory(BlogCategoryFilter category) {
        this.category = category;
    }

    public BooleanFilter getPublished() {
        return published;
    }

    public Optional<BooleanFilter> optionalPublished() {
        return Optional.ofNullable(published);
    }

    public BooleanFilter published() {
        if (published == null) {
            setPublished(new BooleanFilter());
        }
        return published;
    }

    public void setPublished(BooleanFilter published) {
        this.published = published;
    }

    public InstantFilter getPublishedAt() {
        return publishedAt;
    }

    public Optional<InstantFilter> optionalPublishedAt() {
        return Optional.ofNullable(publishedAt);
    }

    public InstantFilter publishedAt() {
        if (publishedAt == null) {
            setPublishedAt(new InstantFilter());
        }
        return publishedAt;
    }

    public void setPublishedAt(InstantFilter publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BlogPostCriteria that = (BlogPostCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titleVi, that.titleVi) &&
            Objects.equals(titleEn, that.titleEn) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(excerptVi, that.excerptVi) &&
            Objects.equals(excerptEn, that.excerptEn) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(category, that.category) &&
            Objects.equals(published, that.published) &&
            Objects.equals(publishedAt, that.publishedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titleVi, titleEn, slug, excerptVi, excerptEn, coverImageUrl, category, published, publishedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPostCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitleVi().map(f -> "titleVi=" + f + ", ").orElse("") +
            optionalTitleEn().map(f -> "titleEn=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalExcerptVi().map(f -> "excerptVi=" + f + ", ").orElse("") +
            optionalExcerptEn().map(f -> "excerptEn=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalPublished().map(f -> "published=" + f + ", ").orElse("") +
            optionalPublishedAt().map(f -> "publishedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
