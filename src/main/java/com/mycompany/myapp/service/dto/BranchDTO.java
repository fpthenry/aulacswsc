package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Branch} entity.
 */
@Schema(description = "Cơ sở / chi nhánh spa")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BranchDTO implements Serializable {

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

    @NotNull
    @Size(max = 300)
    private String addressVi;

    @Size(max = 300)
    private String addressEn;

    @NotNull
    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String whatsapp;

    @Size(max = 20)
    private String zalo;

    @Size(max = 150)
    private String email;

    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Schema(description = "Giờ mở cửa dạng HH:mm, vd 10:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String openTime;

    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Schema(description = "Giờ đóng cửa dạng HH:mm, vd 22:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String closeTime;

    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Schema(description = "Khung giờ nhận khách cuối cùng, vd 21:30", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastBookingTime;

    @NotNull
    @Min(value = 1)
    @Schema(description = "Số khách tối đa phục vụ đồng thời", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer capacity;

    @Size(max = 1000)
    private String mapEmbedUrl;

    @Size(max = 500)
    private String mapDirectionUrl;

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

    public String getAddressVi() {
        return addressVi;
    }

    public void setAddressVi(String addressVi) {
        this.addressVi = addressVi;
    }

    public String getAddressEn() {
        return addressEn;
    }

    public void setAddressEn(String addressEn) {
        this.addressEn = addressEn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getZalo() {
        return zalo;
    }

    public void setZalo(String zalo) {
        this.zalo = zalo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getLastBookingTime() {
        return lastBookingTime;
    }

    public void setLastBookingTime(String lastBookingTime) {
        this.lastBookingTime = lastBookingTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getMapEmbedUrl() {
        return mapEmbedUrl;
    }

    public void setMapEmbedUrl(String mapEmbedUrl) {
        this.mapEmbedUrl = mapEmbedUrl;
    }

    public String getMapDirectionUrl() {
        return mapDirectionUrl;
    }

    public void setMapDirectionUrl(String mapDirectionUrl) {
        this.mapDirectionUrl = mapDirectionUrl;
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
        if (!(o instanceof BranchDTO)) {
            return false;
        }

        BranchDTO branchDTO = (BranchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, branchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BranchDTO{" +
            "id=" + getId() +
            ", nameVi='" + getNameVi() + "'" +
            ", nameEn='" + getNameEn() + "'" +
            ", slug='" + getSlug() + "'" +
            ", addressVi='" + getAddressVi() + "'" +
            ", addressEn='" + getAddressEn() + "'" +
            ", phone='" + getPhone() + "'" +
            ", whatsapp='" + getWhatsapp() + "'" +
            ", zalo='" + getZalo() + "'" +
            ", email='" + getEmail() + "'" +
            ", openTime='" + getOpenTime() + "'" +
            ", closeTime='" + getCloseTime() + "'" +
            ", lastBookingTime='" + getLastBookingTime() + "'" +
            ", capacity=" + getCapacity() +
            ", mapEmbedUrl='" + getMapEmbedUrl() + "'" +
            ", mapDirectionUrl='" + getMapDirectionUrl() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", displayOrder=" + getDisplayOrder() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
