package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Cơ sở / chi nhánh spa
 */
@Entity
@Table(name = "branch")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Branch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(name = "name_vi", length = 150, nullable = false)
    private String nameVi;

    @NotNull
    @Size(max = 150)
    @Column(name = "name_en", length = 150, nullable = false)
    private String nameEn;

    @NotNull
    @Size(max = 180)
    @Column(name = "slug", length = 180, nullable = false, unique = true)
    private String slug;

    @NotNull
    @Size(max = 300)
    @Column(name = "address_vi", length = 300, nullable = false)
    private String addressVi;

    @Size(max = 300)
    @Column(name = "address_en", length = 300)
    private String addressEn;

    @NotNull
    @Size(max = 20)
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Size(max = 20)
    @Column(name = "whatsapp", length = 20)
    private String whatsapp;

    @Size(max = 20)
    @Column(name = "zalo", length = 20)
    private String zalo;

    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Giờ mở cửa dạng HH:mm, vd 10:00
     */
    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Column(name = "open_time", nullable = false)
    private String openTime;

    /**
     * Giờ đóng cửa dạng HH:mm, vd 22:00
     */
    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Column(name = "close_time", nullable = false)
    private String closeTime;

    /**
     * Khung giờ nhận khách cuối cùng, vd 21:30
     */
    @NotNull
    @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$")
    @Column(name = "last_booking_time", nullable = false)
    private String lastBookingTime;

    /**
     * Số khách tối đa phục vụ đồng thời
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Size(max = 1000)
    @Column(name = "map_embed_url", length = 1000)
    private String mapEmbedUrl;

    @Size(max = 500)
    @Column(name = "map_direction_url", length = 500)
    private String mapDirectionUrl;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Branch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameVi() {
        return this.nameVi;
    }

    public Branch nameVi(String nameVi) {
        this.setNameVi(nameVi);
        return this;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public Branch nameEn(String nameEn) {
        this.setNameEn(nameEn);
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSlug() {
        return this.slug;
    }

    public Branch slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAddressVi() {
        return this.addressVi;
    }

    public Branch addressVi(String addressVi) {
        this.setAddressVi(addressVi);
        return this;
    }

    public void setAddressVi(String addressVi) {
        this.addressVi = addressVi;
    }

    public String getAddressEn() {
        return this.addressEn;
    }

    public Branch addressEn(String addressEn) {
        this.setAddressEn(addressEn);
        return this;
    }

    public void setAddressEn(String addressEn) {
        this.addressEn = addressEn;
    }

    public String getPhone() {
        return this.phone;
    }

    public Branch phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsapp() {
        return this.whatsapp;
    }

    public Branch whatsapp(String whatsapp) {
        this.setWhatsapp(whatsapp);
        return this;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getZalo() {
        return this.zalo;
    }

    public Branch zalo(String zalo) {
        this.setZalo(zalo);
        return this;
    }

    public void setZalo(String zalo) {
        this.zalo = zalo;
    }

    public String getEmail() {
        return this.email;
    }

    public Branch email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpenTime() {
        return this.openTime;
    }

    public Branch openTime(String openTime) {
        this.setOpenTime(openTime);
        return this;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return this.closeTime;
    }

    public Branch closeTime(String closeTime) {
        this.setCloseTime(closeTime);
        return this;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getLastBookingTime() {
        return this.lastBookingTime;
    }

    public Branch lastBookingTime(String lastBookingTime) {
        this.setLastBookingTime(lastBookingTime);
        return this;
    }

    public void setLastBookingTime(String lastBookingTime) {
        this.lastBookingTime = lastBookingTime;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Branch capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getMapEmbedUrl() {
        return this.mapEmbedUrl;
    }

    public Branch mapEmbedUrl(String mapEmbedUrl) {
        this.setMapEmbedUrl(mapEmbedUrl);
        return this;
    }

    public void setMapEmbedUrl(String mapEmbedUrl) {
        this.mapEmbedUrl = mapEmbedUrl;
    }

    public String getMapDirectionUrl() {
        return this.mapDirectionUrl;
    }

    public Branch mapDirectionUrl(String mapDirectionUrl) {
        this.setMapDirectionUrl(mapDirectionUrl);
        return this;
    }

    public void setMapDirectionUrl(String mapDirectionUrl) {
        this.mapDirectionUrl = mapDirectionUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Branch imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public Branch displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Branch active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Branch)) {
            return false;
        }
        return getId() != null && getId().equals(((Branch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Branch{" +
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
