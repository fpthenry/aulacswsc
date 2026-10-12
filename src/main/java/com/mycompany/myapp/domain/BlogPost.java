package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.BlogCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Bài viết tin tức / blog
 */
@Entity
@Table(name = "blog_post")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlogPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 250)
    @Column(name = "title_vi", length = 250, nullable = false)
    private String titleVi;

    @Size(max = 250)
    @Column(name = "title_en", length = 250)
    private String titleEn;

    @NotNull
    @Size(max = 280)
    @Column(name = "slug", length = 280, nullable = false, unique = true)
    private String slug;

    @Size(max = 500)
    @Column(name = "excerpt_vi", length = 500)
    private String excerptVi;

    @Size(max = 500)
    @Column(name = "excerpt_en", length = 500)
    private String excerptEn;

    @Lob
    @Column(name = "content_vi")
    private String contentVi;

    @Lob
    @Column(name = "content_en")
    private String contentEn;

    @Size(max = 500)
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private BlogCategory category;

    @NotNull
    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "published_at")
    private Instant publishedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BlogPost id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleVi() {
        return this.titleVi;
    }

    public BlogPost titleVi(String titleVi) {
        this.setTitleVi(titleVi);
        return this;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitleEn() {
        return this.titleEn;
    }

    public BlogPost titleEn(String titleEn) {
        this.setTitleEn(titleEn);
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getSlug() {
        return this.slug;
    }

    public BlogPost slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getExcerptVi() {
        return this.excerptVi;
    }

    public BlogPost excerptVi(String excerptVi) {
        this.setExcerptVi(excerptVi);
        return this;
    }

    public void setExcerptVi(String excerptVi) {
        this.excerptVi = excerptVi;
    }

    public String getExcerptEn() {
        return this.excerptEn;
    }

    public BlogPost excerptEn(String excerptEn) {
        this.setExcerptEn(excerptEn);
        return this;
    }

    public void setExcerptEn(String excerptEn) {
        this.excerptEn = excerptEn;
    }

    public String getContentVi() {
        return this.contentVi;
    }

    public BlogPost contentVi(String contentVi) {
        this.setContentVi(contentVi);
        return this;
    }

    public void setContentVi(String contentVi) {
        this.contentVi = contentVi;
    }

    public String getContentEn() {
        return this.contentEn;
    }

    public BlogPost contentEn(String contentEn) {
        this.setContentEn(contentEn);
        return this;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public BlogPost coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public BlogCategory getCategory() {
        return this.category;
    }

    public BlogPost category(BlogCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(BlogCategory category) {
        this.category = category;
    }

    public Boolean getPublished() {
        return this.published;
    }

    public BlogPost published(Boolean published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public BlogPost publishedAt(Instant publishedAt) {
        this.setPublishedAt(publishedAt);
        return this;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPost)) {
            return false;
        }
        return getId() != null && getId().equals(((BlogPost) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlogPost{" +
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
