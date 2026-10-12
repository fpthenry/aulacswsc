package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.SpaService} entity.
 */
@Schema(description = "Dịch vụ spa")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpaServiceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String nameVi;

    @NotNull
    @Size(max = 200)
    private String nameEn;

    /** do server sinh khi tạo mới (SlugService) */
    @Size(max = 220)
    private String slug;

    @Size(max = 500)
    private String shortDescriptionVi;

    @Size(max = 500)
    private String shortDescriptionEn;

    @Lob
    private String descriptionVi;

    @Lob
    private String descriptionEn;

    @Schema(description = "Công dụng / lợi ích, mỗi dòng một ý")
    @Lob
    private String benefitsVi;

    @Lob
    private String benefitsEn;

    @Schema(description = "Lưu ý, mỗi dòng một ý")
    @Lob
    private String notesVi;

    @Lob
    private String notesEn;

    @NotNull
    @DecimalMin(value = "0")
    @Schema(description = "Giá gốc (hoặc giá thấp nhất nếu là khoảng giá)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @DecimalMin(value = "0")
    @Schema(description = "Giá khuyến mãi, để trống nếu không giảm")
    private BigDecimal salePrice;

    @DecimalMin(value = "0")
    @Schema(description = "Giá cao nhất nếu hiển thị dạng khoảng giá")
    private BigDecimal priceMax;

    @NotNull
    @Min(value = 15)
    private Integer durationMinutes;

    @Size(max = 500)
    private String imageUrl;

    private Boolean featured;

    private Integer displayOrder;

    @NotNull
    private Boolean active;

    @NotNull
    private ServiceCategoryDTO category;

    private Set<StaffDTO> staffs = new HashSet<>();

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

    public String getShortDescriptionVi() {
        return shortDescriptionVi;
    }

    public void setShortDescriptionVi(String shortDescriptionVi) {
        this.shortDescriptionVi = shortDescriptionVi;
    }

    public String getShortDescriptionEn() {
        return shortDescriptionEn;
    }

    public void setShortDescriptionEn(String shortDescriptionEn) {
        this.shortDescriptionEn = shortDescriptionEn;
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

    public String getBenefitsVi() {
        return benefitsVi;
    }

    public void setBenefitsVi(String benefitsVi) {
        this.benefitsVi = benefitsVi;
    }

    public String getBenefitsEn() {
        return benefitsEn;
    }

    public void setBenefitsEn(String benefitsEn) {
        this.benefitsEn = benefitsEn;
    }

    public String getNotesVi() {
        return notesVi;
    }

    public void setNotesVi(String notesVi) {
        this.notesVi = notesVi;
    }

    public String getNotesEn() {
        return notesEn;
    }

    public void setNotesEn(String notesEn) {
        this.notesEn = notesEn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(BigDecimal priceMax) {
        this.priceMax = priceMax;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
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

    public ServiceCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(ServiceCategoryDTO category) {
        this.category = category;
    }

    public Set<StaffDTO> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<StaffDTO> staffs) {
        this.staffs = staffs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaServiceDTO)) {
            return false;
        }

        SpaServiceDTO spaServiceDTO = (SpaServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, spaServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpaServiceDTO{" +
            "id=" + getId() +
            ", nameVi='" + getNameVi() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", slug='" + getSlug() + "'" +
            ", shortDescriptionVi='" + getShortDescriptionVi() + "'" +
            ", shortDescriptionEn='" + getShortDescriptionEn() + "'" +
            ", descriptionVi='" + getDescriptionVi() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", benefitsVi='" + getBenefitsVi() + "'" +
            ", benefitsEn='" + getBenefitsEn() + "'" +
            ", notesVi='" + getNotesVi() + "'" +
            ", notesEn='" + getNotesEn() + "'" +
            ", price=" + getPrice() +
            ", salePrice=" + getSalePrice() +
            ", priceMax=" + getPriceMax() +
            ", durationMinutes=" + getDurationMinutes() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", featured='" + getFeatured() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", active='" + getActive() + "'" +
            ", category=" + getCategory() +
            ", staffs=" + getStaffs() +
            "}";
    }
}
