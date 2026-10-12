package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Ảnh thư viện
 */
@Entity
@Table(name = "gallery_image")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GalleryImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Size(max = 300)
    @Column(name = "caption_vi", length = 300)
    private String captionVi;

    @Size(max = 300)
    @Column(name = "caption_en", length = 300)
    private String captionEn;

    @NotNull
    @Size(max = 300)
    @Column(name = "alt_vi", length = 300, nullable = false)
    private String altVi;

    @Size(max = 300)
    @Column(name = "alt_en", length = 300)
    private String altEn;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private Branch branch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GalleryImage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public GalleryImage imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaptionVi() {
        return this.captionVi;
    }

    public GalleryImage captionVi(String captionVi) {
        this.setCaptionVi(captionVi);
        return this;
    }

    public void setCaptionVi(String captionVi) {
        this.captionVi = captionVi;
    }

    public String getCaptionEn() {
        return this.captionEn;
    }

    public GalleryImage captionEn(String captionEn) {
        this.setCaptionEn(captionEn);
        return this;
    }

    public void setCaptionEn(String captionEn) {
        this.captionEn = captionEn;
    }

    public String getAltVi() {
        return this.altVi;
    }

    public GalleryImage altVi(String altVi) {
        this.setAltVi(altVi);
        return this;
    }

    public void setAltVi(String altVi) {
        this.altVi = altVi;
    }

    public String getAltEn() {
        return this.altEn;
    }

    public GalleryImage altEn(String altEn) {
        this.setAltEn(altEn);
        return this;
    }

    public void setAltEn(String altEn) {
        this.altEn = altEn;
    }

    public Integer getWidth() {
        return this.width;
    }

    public GalleryImage width(Integer width) {
        this.setWidth(width);
        return this;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public GalleryImage height(Integer height) {
        this.setHeight(height);
        return this;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public GalleryImage displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return this.active;
    }

    public GalleryImage active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Branch getBranch() {
        return this.branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public GalleryImage branch(Branch branch) {
        this.setBranch(branch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GalleryImage)) {
            return false;
        }
        return getId() != null && getId().equals(((GalleryImage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GalleryImage{" +
            "id=" + getId() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", captionVi='" + getCaptionVi() + "'" +
            ", captionEn='" + getCaptionEn() + "'" +
            ", altVi='" + getAltVi() + "'" +
            ", altEn='" + getAltEn() + "'" +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            ", displayOrder=" + getDisplayOrder() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
