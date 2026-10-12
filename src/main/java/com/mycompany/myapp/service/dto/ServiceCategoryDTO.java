package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ServiceCategory} entity.
 */
@Schema(description = "Danh mục dịch vụ (Massage, Chăm sóc tóc, Chăm sóc da, Combo...)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String nameVi;

    @NotNull
    @Size(max = 150)
    private String nameEn;

    /** do server sinh khi tạo mới (SlugService) */
    @Size(max = 180)
    private String slug;

    @Size(max = 1000)
    private String descriptionVi;

    @Size(max = 1000)
    private String descriptionEn;

    @Size(max = 500)
    private String imageUrl;

    private Integer displayOrder;

    @NotNull
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameVi() {
        return nameVi;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceCategoryDTO)) {
            return false;
        }

        ServiceCategoryDTO serviceCategoryDTO = (ServiceCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceCategoryDTO{" +
            "id=" + getId() +
            ", nameVi='" + getNameVi() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", slug='" + getSlug() + "'" +
            ", descriptionVi='" + getDescriptionVi() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
