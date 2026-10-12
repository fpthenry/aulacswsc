package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Dịch vụ spa
 */
@Entity
@Table(name = "spa_service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpaService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "name_vi", length = 200, nullable = false)
    private String nameVi;

    @NotNull
    @Size(max = 200)
    @Column(name = "name_en", length = 200, nullable = false)
    private String nameEn;

    @NotNull
    @Size(max = 220)
    @Column(name = "slug", length = 220, nullable = false, unique = true)
    private String slug;

    @Size(max = 500)
    @Column(name = "short_description_vi", length = 500)
    private String shortDescriptionVi;

    @Size(max = 500)
    @Column(name = "short_description_en", length = 500)
    private String shortDescriptionEn;

    @Lob
    @Column(name = "description_vi")
    private String descriptionVi;

    @Lob
    @Column(name = "description_en")
    private String descriptionEn;

    /**
     * Công dụng / lợi ích, mỗi dòng một ý
     */
    @Lob
    @Column(name = "benefits_vi")
    private String benefitsVi;

    @Lob
    @Column(name = "benefits_en")
    private String benefitsEn;

    /**
     * Lưu ý, mỗi dòng một ý
     */
    @Lob
    @Column(name = "notes_vi")
    private String notesVi;

    @Lob
    @Column(name = "notes_en")
    private String notesEn;

    /**
     * Giá gốc (hoặc giá thấp nhất nếu là khoảng giá)
     */
    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * Giá khuyến mãi, để trống nếu không giảm
     */
    @DecimalMin(value = "0")
    @Column(name = "sale_price", precision = 21, scale = 2)
    private BigDecimal salePrice;

    /**
     * Giá cao nhất nếu hiển thị dạng khoảng giá
     */
    @DecimalMin(value = "0")
    @Column(name = "price_max", precision = 21, scale = 2)
    private BigDecimal priceMax;

    @NotNull
    @Min(value = 15)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "featured")
    private Boolean featured;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "services" }, allowSetters = true)
    private ServiceCategory category;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "services")
    @JsonIgnoreProperties(value = { "user", "branch", "services" }, allowSetters = true)
    private Set<Staff> staffs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SpaService id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameVi() {
        return this.nameVi;
    }

    public SpaService nameVi(String nameVi) {
        this.setNameVi(nameVi);
        return this;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public SpaService nameEn(String nameEn) {
        this.setNameEn(nameEn);
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSlug() {
        return this.slug;
    }

    public SpaService slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getShortDescriptionVi() {
        return this.shortDescriptionVi;
    }

    public SpaService shortDescriptionVi(String shortDescriptionVi) {
        this.setShortDescriptionVi(shortDescriptionVi);
        return this;
    }

    public void setShortDescriptionVi(String shortDescriptionVi) {
        this.shortDescriptionVi = shortDescriptionVi;
    }

    public String getShortDescriptionEn() {
        return this.shortDescriptionEn;
    }

    public SpaService shortDescriptionEn(String shortDescriptionEn) {
        this.setShortDescriptionEn(shortDescriptionEn);
        return this;
    }

    public void setShortDescriptionEn(String shortDescriptionEn) {
        this.shortDescriptionEn = shortDescriptionEn;
    }

    public String getDescriptionVi() {
        return this.descriptionVi;
    }

    public SpaService descriptionVi(String descriptionVi) {
        this.setDescriptionVi(descriptionVi);
        return this;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public SpaService descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getBenefitsVi() {
        return this.benefitsVi;
    }

    public SpaService benefitsVi(String benefitsVi) {
        this.setBenefitsVi(benefitsVi);
        return this;
    }

    public void setBenefitsVi(String benefitsVi) {
        this.benefitsVi = benefitsVi;
    }

    public String getBenefitsEn() {
        return this.benefitsEn;
    }

    public SpaService benefitsEn(String benefitsEn) {
        this.setBenefitsEn(benefitsEn);
        return this;
    }

    public void setBenefitsEn(String benefitsEn) {
        this.benefitsEn = benefitsEn;
    }

    public String getNotesVi() {
        return this.notesVi;
    }

    public SpaService notesVi(String notesVi) {
        this.setNotesVi(notesVi);
        return this;
    }

    public void setNotesVi(String notesVi) {
        this.notesVi = notesVi;
    }

    public String getNotesEn() {
        return this.notesEn;
    }

    public SpaService notesEn(String notesEn) {
        this.setNotesEn(notesEn);
        return this;
    }

    public void setNotesEn(String notesEn) {
        this.notesEn = notesEn;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public SpaService price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    public SpaService salePrice(BigDecimal salePrice) {
        this.setSalePrice(salePrice);
        return this;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getPriceMax() {
        return this.priceMax;
    }

    public SpaService priceMax(BigDecimal priceMax) {
        this.setPriceMax(priceMax);
        return this;
    }

    public void setPriceMax(BigDecimal priceMax) {
        this.priceMax = priceMax;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public SpaService durationMinutes(Integer durationMinutes) {
        this.setDurationMinutes(durationMinutes);
        return this;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public SpaService imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFeatured() {
        return this.featured;
    }

    public SpaService featured(Boolean featured) {
        this.setFeatured(featured);
        return this;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public SpaService displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SpaService active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ServiceCategory getCategory() {
        return this.category;
    }

    public void setCategory(ServiceCategory serviceCategory) {
        this.category = serviceCategory;
    }

    public SpaService category(ServiceCategory serviceCategory) {
        this.setCategory(serviceCategory);
        return this;
    }

    public Set<Staff> getStaffs() {
        return this.staffs;
    }

    public void setStaffs(Set<Staff> staff) {
        if (this.staffs != null) {
            this.staffs.forEach(i -> i.removeServices(this));
        }
        if (staff != null) {
            staff.forEach(i -> i.addServices(this));
        }
        this.staffs = staff;
    }

    public SpaService staffs(Set<Staff> staff) {
        this.setStaffs(staff);
        return this;
    }

    public SpaService addStaffs(Staff staff) {
        this.staffs.add(staff);
        staff.getServices().add(this);
        return this;
    }

    public SpaService removeStaffs(Staff staff) {
        this.staffs.remove(staff);
        staff.getServices().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaService)) {
            return false;
        }
        return getId() != null && getId().equals(((SpaService) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpaService{" +
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
            "}";
    }
}
