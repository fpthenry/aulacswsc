package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.GalleryImage} entity.
 */
@Schema(description = "Ảnh thư viện")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GalleryImageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 500)
    private String imageUrl;

    @Size(max = 300)
    private String captionVi;

    @Size(max = 300)
    private String captionEn;

    @NotNull
    @Size(max = 300)
    private String altVi;

    @Size(max = 300)
    private String altEn;

    private Integer width;

    private Integer height;

    private Integer displayOrder;

    @NotNull
    private Boolean active;

    private BranchDTO branch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaptionVi() {
        return captionVi;
    }

    public void setCaptionVi(String captionVi) {
        this.captionVi = captionVi;
    }

    public String getCaptionEn() {
        return captionEn;
    }

    public void setCaptionEn(String captionEn) {
        this.captionEn = captionEn;
    }

    public String getAltVi() {
        return altVi;
    }

    public void setAltVi(String altVi) {
        this.altVi = altVi;
    }

    public String getAltEn() {
        return altEn;
    }

    public void setAltEn(String altEn) {
        this.altEn = altEn;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BranchDTO getBranch() {
        return branch;
    }

    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GalleryImageDTO)) {
            return false;
        }

        GalleryImageDTO galleryImageDTO = (GalleryImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, galleryImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GalleryImageDTO{" +
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
            ", branch=" + getBranch() +
            "}";
    }
}
