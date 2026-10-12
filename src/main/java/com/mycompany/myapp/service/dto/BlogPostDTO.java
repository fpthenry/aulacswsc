package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.BlogCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BlogPost} entity.
 */
@Schema(description = "Bài viết tin tức / blog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPostDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 250)
    private String titleVi;

    @Size(max = 250)
    private String titleEn;

    /** do server sinh khi tạo mới (SlugService) */
    @Size(max = 280)
    private String slug;

    @Size(max = 500)
    private String excerptVi;

    @Size(max = 500)
    private String excerptEn;

    @Lob
    private String contentVi;

    @Lob
    private String contentEn;

    @Size(max = 500)
    private String coverImageUrl;

    private BlogCategory category;

    @NotNull
    private Boolean published;

    private Instant publishedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getExcerptVi() {
        return excerptVi;
    }

    public void setExcerptVi(String excerptVi) {
        this.excerptVi = excerptVi;
    }

    public String getExcerptEn() {
        return excerptEn;
    }

    public void setExcerptEn(String excerptEn) {
        this.excerptEn = excerptEn;
    }

    public String getContentVi() {
        return contentVi;
    }

    public void setContentVi(String contentVi) {
        this.contentVi = contentVi;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public BlogCategory getCategory() {
        return category;
    }

    public void setCategory(BlogCategory category) {
        this.category = category;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPostDTO)) {
            return false;
        }

        BlogPostDTO blogPostDTO = (BlogPostDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, blogPostDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPostDTO{" +
            "id=" + getId() +
            ", titleVi='" + getTitleVi() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            ", slug='" + getSlug() + "'" +
            ", excerptVi='" + getExcerptVi() + "'" +
            ", excerptEn='" + getExcerptEn() + "'" +
            ", contentVi='" + getContentVi() + "'" +
            ", contentEn='" + getContentEn() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", category='" + getCategory() + "'" +
            ", published='" + getPublished() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            "}";
    }
}
