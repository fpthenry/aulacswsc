package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Dịch vụ spa cụ thể
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
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotNull
    @Size(max = 220)
    @Column(name = "slug", length = 220, nullable = false, unique = true)
    private String slug;

    @Size(max = 500)
    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @DecimalMin(value = "0")
    @Column(name = "sale_price", precision = 21, scale = 2)
    private BigDecimal salePrice;

    @NotNull
    @Min(value = 5)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "featured")
    private Boolean featured;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "services" }, allowSetters = true)
    private ServiceCategory category;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "services")
    @JsonIgnoreProperties(value = { "user", "services" }, allowSetters = true)
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

    public String getName() {
        return this.name;
    }

    public SpaService name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getShortDescription() {
        return this.shortDescription;
    }

    public SpaService shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return this.description;
    }

    public SpaService description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
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
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", salePrice=" + getSalePrice() +
            ", durationMinutes=" + getDurationMinutes() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", featured='" + getFeatured() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
