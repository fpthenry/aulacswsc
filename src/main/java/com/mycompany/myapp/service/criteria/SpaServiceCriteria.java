package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.SpaService} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.SpaServiceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /spa-services?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SpaServiceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nameVi;

    private StringFilter nameEn;

    private StringFilter slug;

    private StringFilter shortDescriptionVi;

    private StringFilter shortDescriptionEn;

    private BigDecimalFilter price;

    private BigDecimalFilter salePrice;

    private BigDecimalFilter priceMax;

    private IntegerFilter durationMinutes;

    private StringFilter imageUrl;

    private BooleanFilter featured;

    private IntegerFilter displayOrder;

    private BooleanFilter active;

    private LongFilter categoryId;

    private LongFilter staffsId;

    private Boolean distinct;

    public SpaServiceCriteria() {}

    public SpaServiceCriteria(SpaServiceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nameVi = other.optionalNameVi().map(StringFilter::copy).orElse(null);
        this.nameEn = other.optionalNameEn().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.shortDescriptionVi = other.optionalShortDescriptionVi().map(StringFilter::copy).orElse(null);
        this.shortDescriptionEn = other.optionalShortDescriptionEn().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.salePrice = other.optionalSalePrice().map(BigDecimalFilter::copy).orElse(null);
        this.priceMax = other.optionalPriceMax().map(BigDecimalFilter::copy).orElse(null);
        this.durationMinutes = other.optionalDurationMinutes().map(IntegerFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.featured = other.optionalFeatured().map(BooleanFilter::copy).orElse(null);
        this.displayOrder = other.optionalDisplayOrder().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.staffsId = other.optionalStaffsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SpaServiceCriteria copy() {
        return new SpaServiceCriteria(this);
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

    public StringFilter getShortDescriptionVi() {
        return shortDescriptionVi;
    }

    public Optional<StringFilter> optionalShortDescriptionVi() {
        return Optional.ofNullable(shortDescriptionVi);
    }

    public StringFilter shortDescriptionVi() {
        if (shortDescriptionVi == null) {
            setShortDescriptionVi(new StringFilter());
        }
        return shortDescriptionVi;
    }

    public void setShortDescriptionVi(StringFilter shortDescriptionVi) {
        this.shortDescriptionVi = shortDescriptionVi;
    }

    public StringFilter getShortDescriptionEn() {
        return shortDescriptionEn;
    }

    public Optional<StringFilter> optionalShortDescriptionEn() {
        return Optional.ofNullable(shortDescriptionEn);
    }

    public StringFilter shortDescriptionEn() {
        if (shortDescriptionEn == null) {
            setShortDescriptionEn(new StringFilter());
        }
        return shortDescriptionEn;
    }

    public void setShortDescriptionEn(StringFilter shortDescriptionEn) {
        this.shortDescriptionEn = shortDescriptionEn;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getSalePrice() {
        return salePrice;
    }

    public Optional<BigDecimalFilter> optionalSalePrice() {
        return Optional.ofNullable(salePrice);
    }

    public BigDecimalFilter salePrice() {
        if (salePrice == null) {
            setSalePrice(new BigDecimalFilter());
        }
        return salePrice;
    }

    public void setSalePrice(BigDecimalFilter salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimalFilter getPriceMax() {
        return priceMax;
    }

    public Optional<BigDecimalFilter> optionalPriceMax() {
        return Optional.ofNullable(priceMax);
    }

    public BigDecimalFilter priceMax() {
        if (priceMax == null) {
            setPriceMax(new BigDecimalFilter());
        }
        return priceMax;
    }

    public void setPriceMax(BigDecimalFilter priceMax) {
        this.priceMax = priceMax;
    }

    public IntegerFilter getDurationMinutes() {
        return durationMinutes;
    }

    public Optional<IntegerFilter> optionalDurationMinutes() {
        return Optional.ofNullable(durationMinutes);
    }

    public IntegerFilter durationMinutes() {
        if (durationMinutes == null) {
            setDurationMinutes(new IntegerFilter());
        }
        return durationMinutes;
    }

    public void setDurationMinutes(IntegerFilter durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public BooleanFilter getFeatured() {
        return featured;
    }

    public Optional<BooleanFilter> optionalFeatured() {
        return Optional.ofNullable(featured);
    }

    public BooleanFilter featured() {
        if (featured == null) {
            setFeatured(new BooleanFilter());
        }
        return featured;
    }

    public void setFeatured(BooleanFilter featured) {
        this.featured = featured;
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

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getStaffsId() {
        return staffsId;
    }

    public Optional<LongFilter> optionalStaffsId() {
        return Optional.ofNullable(staffsId);
    }

    public LongFilter staffsId() {
        if (staffsId == null) {
            setStaffsId(new LongFilter());
        }
        return staffsId;
    }

    public void setStaffsId(LongFilter staffsId) {
        this.staffsId = staffsId;
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
        final SpaServiceCriteria that = (SpaServiceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nameVi, that.nameVi) &&
            Objects.equals(nameEn, that.nameEn) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(shortDescriptionVi, that.shortDescriptionVi) &&
            Objects.equals(shortDescriptionEn, that.shortDescriptionEn) &&
            Objects.equals(price, that.price) &&
            Objects.equals(salePrice, that.salePrice) &&
            Objects.equals(priceMax, that.priceMax) &&
            Objects.equals(durationMinutes, that.durationMinutes) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(featured, that.featured) &&
            Objects.equals(displayOrder, that.displayOrder) &&
            Objects.equals(active, that.active) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(staffsId, that.staffsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            nameVi,
            nameEn,
            slug,
            shortDescriptionVi,
            shortDescriptionEn,
            price,
            salePrice,
            priceMax,
            durationMinutes,
            imageUrl,
            featured,
            displayOrder,
            active,
            categoryId,
            staffsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SpaServiceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNameVi().map(f -> "nameVi=" + f + ", ").orElse("") +
            optionalNameEn().map(f -> "nameEn=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalShortDescriptionVi().map(f -> "shortDescriptionVi=" + f + ", ").orElse("") +
            optionalShortDescriptionEn().map(f -> "shortDescriptionEn=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalSalePrice().map(f -> "salePrice=" + f + ", ").orElse("") +
            optionalPriceMax().map(f -> "priceMax=" + f + ", ").orElse("") +
            optionalDurationMinutes().map(f -> "durationMinutes=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalFeatured().map(f -> "featured=" + f + ", ").orElse("") +
            optionalDisplayOrder().map(f -> "displayOrder=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalStaffsId().map(f -> "staffsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
