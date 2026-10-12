package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.ServiceCategory} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ServiceCategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /service-categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceCategoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nameVi;

    private StringFilter nameEn;

    private StringFilter slug;

    private StringFilter descriptionVi;

    private StringFilter descriptionEn;

    private StringFilter imageUrl;

    private IntegerFilter displayOrder;

    private BooleanFilter active;

    private LongFilter servicesId;

    private Boolean distinct;

    public ServiceCategoryCriteria() {}

    public ServiceCategoryCriteria(ServiceCategoryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nameVi = other.optionalNameVi().map(StringFilter::copy).orElse(null);
        this.nameEn = other.optionalNameEn().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.descriptionVi = other.optionalDescriptionVi().map(StringFilter::copy).orElse(null);
        this.descriptionEn = other.optionalDescriptionEn().map(StringFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.displayOrder = other.optionalDisplayOrder().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.servicesId = other.optionalServicesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ServiceCategoryCriteria copy() {
        return new ServiceCategoryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNameVi() {
        return nameVi;
    }

    public Optional<StringFilter> optionalNameVi() {
        return Optional.ofNullable(nameVi);
    }

    public StringFilter nameVi() {
        if (nameVi == null) {
            setNameVi(new StringFilter());
        }
        return nameVi;
    }

    public void setNameVi(StringFilter nameVi) {
        this.nameVi = nameVi;
    }

    public StringFilter getNameEn() {
        return nameEn;
    }

    public Optional<StringFilter> optionalNameEn() {
        return Optional.ofNullable(nameEn);
    }

    public StringFilter nameEn() {
        if (nameEn == null) {
            setNameEn(new StringFilter());
        }
        return nameEn;
    }

    public void setNameEn(StringFilter nameEn) {
        this.nameEn = nameEn;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public StringFilter getDescriptionVi() {
        return descriptionVi;
    }

    public Optional<StringFilter> optionalDescriptionVi() {
        return Optional.ofNullable(descriptionVi);
    }

    public StringFilter descriptionVi() {
        if (descriptionVi == null) {
            setDescriptionVi(new StringFilter());
        }
        return descriptionVi;
    }

    public void setDescriptionVi(StringFilter descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public StringFilter getDescriptionEn() {
        return descriptionEn;
    }

    public Optional<StringFilter> optionalDescriptionEn() {
        return Optional.ofNullable(descriptionEn);
    }

    public StringFilter descriptionEn() {
        if (descriptionEn == null) {
            setDescriptionEn(new StringFilter());
        }
        return descriptionEn;
    }

    public void setDescriptionEn(StringFilter descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public IntegerFilter getDisplayOrder() {
        return displayOrder;
    }

    public Optional<IntegerFilter> optionalDisplayOrder() {
        return Optional.ofNullable(displayOrder);
    }

    public IntegerFilter displayOrder() {
        if (displayOrder == null) {
            setDisplayOrder(new IntegerFilter());
        }
        return displayOrder;
    }

    public void setDisplayOrder(IntegerFilter displayOrder) {
        this.displayOrder = displayOrder;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public LongFilter getServicesId() {
        return servicesId;
    }

    public Optional<LongFilter> optionalServicesId() {
        return Optional.ofNullable(servicesId);
    }

    public LongFilter servicesId() {
        if (servicesId == null) {
            setServicesId(new LongFilter());
        }
        return servicesId;
    }

    public void setServicesId(LongFilter servicesId) {
        this.servicesId = servicesId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServiceCategoryCriteria that = (ServiceCategoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nameVi, that.nameVi) &&
            Objects.equals(nameEn, that.nameEn) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(descriptionVi, that.descriptionVi) &&
            Objects.equals(descriptionEn, that.descriptionEn) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(displayOrder, that.displayOrder) &&
            Objects.equals(active, that.active) &&
            Objects.equals(servicesId, that.servicesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameVi, nameEn, slug, descriptionVi, descriptionEn, imageUrl, displayOrder, active, servicesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceCategoryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNameVi().map(f -> "nameVi=" + f + ", ").orElse("") +
            optionalNameEn().map(f -> "nameEn=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalDescriptionVi().map(f -> "descriptionVi=" + f + ", ").orElse("") +
            optionalDescriptionEn().map(f -> "descriptionEn=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalDisplayOrder().map(f -> "displayOrder=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalServicesId().map(f -> "servicesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
