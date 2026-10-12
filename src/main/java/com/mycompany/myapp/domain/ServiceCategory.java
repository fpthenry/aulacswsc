package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Danh mục dịch vụ (Massage, Chăm sóc tóc, Chăm sóc da, Combo...)
 */
@Entity
@Table(name = "service_category")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceCategory implements Serializable {

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

    @Size(max = 1000)
    @Column(name = "description_vi", length = 1000)
    private String descriptionVi;

    @Size(max = 1000)
    @Column(name = "description_en", length = 1000)
    private String descriptionEn;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @JsonIgnoreProperties(value = { "category", "staffs" }, allowSetters = true)
    private Set<SpaService> services = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameVi() {
        return this.nameVi;
    }

    public ServiceCategory nameVi(String nameVi) {
        this.setNameVi(nameVi);
        return this;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public ServiceCategory nameEn(String nameEn) {
        this.setNameEn(nameEn);
        return this;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSlug() {
        return this.slug;
    }

    public ServiceCategory slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescriptionVi() {
        return this.descriptionVi;
    }

    public ServiceCategory descriptionVi(String descriptionVi) {
        this.setDescriptionVi(descriptionVi);
        return this;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public ServiceCategory descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ServiceCategory imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return this.displayOrder;
    }

    public ServiceCategory displayOrder(Integer displayOrder) {
        this.setDisplayOrder(displayOrder);
        return this;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ServiceCategory active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<SpaService> getServices() {
        return this.services;
    }

    public void setServices(Set<SpaService> spaServices) {
        if (this.services != null) {
            this.services.forEach(i -> i.setCategory(null));
        }
        if (spaServices != null) {
            spaServices.forEach(i -> i.setCategory(this));
        }
        this.services = spaServices;
    }

    public ServiceCategory services(Set<SpaService> spaServices) {
        this.setServices(spaServices);
        return this;
    }

    public ServiceCategory addServices(SpaService spaService) {
        this.services.add(spaService);
        spaService.setCategory(this);
        return this;
    }

    public ServiceCategory removeServices(SpaService spaService) {
        this.services.remove(spaService);
        spaService.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceCategory)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceCategory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceCategory{" +
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
