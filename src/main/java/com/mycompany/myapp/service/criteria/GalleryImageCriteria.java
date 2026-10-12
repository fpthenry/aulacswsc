package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.GalleryImage} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.GalleryImageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /gallery-images?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GalleryImageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter imageUrl;

    private StringFilter captionVi;

    private StringFilter captionEn;

    private StringFilter altVi;

    private StringFilter altEn;

    private IntegerFilter width;

    private IntegerFilter height;

    private IntegerFilter displayOrder;

    private BooleanFilter active;

    private LongFilter branchId;

    private Boolean distinct;

    public GalleryImageCriteria() {}

    public GalleryImageCriteria(GalleryImageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.captionVi = other.optionalCaptionVi().map(StringFilter::copy).orElse(null);
        this.captionEn = other.optionalCaptionEn().map(StringFilter::copy).orElse(null);
        this.altVi = other.optionalAltVi().map(StringFilter::copy).orElse(null);
        this.altEn = other.optionalAltEn().map(StringFilter::copy).orElse(null);
        this.width = other.optionalWidth().map(IntegerFilter::copy).orElse(null);
        this.height = other.optionalHeight().map(IntegerFilter::copy).orElse(null);
        this.displayOrder = other.optionalDisplayOrder().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.branchId = other.optionalBranchId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public GalleryImageCriteria copy() {
        return new GalleryImageCriteria(this);
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

    public StringFilter getCaptionVi() {
        return captionVi;
    }

    public Optional<StringFilter> optionalCaptionVi() {
        return Optional.ofNullable(captionVi);
    }

    public StringFilter captionVi() {
        if (captionVi == null) {
            setCaptionVi(new StringFilter());
        }
        return captionVi;
    }

    public void setCaptionVi(StringFilter captionVi) {
        this.captionVi = captionVi;
    }

    public StringFilter getCaptionEn() {
        return captionEn;
    }

    public Optional<StringFilter> optionalCaptionEn() {
        return Optional.ofNullable(captionEn);
    }

    public StringFilter captionEn() {
        if (captionEn == null) {
            setCaptionEn(new StringFilter());
        }
        return captionEn;
    }

    public void setCaptionEn(StringFilter captionEn) {
        this.captionEn = captionEn;
    }

    public StringFilter getAltVi() {
        return altVi;
    }

    public Optional<StringFilter> optionalAltVi() {
        return Optional.ofNullable(altVi);
    }

    public StringFilter altVi() {
        if (altVi == null) {
            setAltVi(new StringFilter());
        }
        return altVi;
    }

    public void setAltVi(StringFilter altVi) {
        this.altVi = altVi;
    }

    public StringFilter getAltEn() {
        return altEn;
    }

    public Optional<StringFilter> optionalAltEn() {
        return Optional.ofNullable(altEn);
    }

    public StringFilter altEn() {
        if (altEn == null) {
            setAltEn(new StringFilter());
        }
        return altEn;
    }

    public void setAltEn(StringFilter altEn) {
        this.altEn = altEn;
    }

    public IntegerFilter getWidth() {
        return width;
    }

    public Optional<IntegerFilter> optionalWidth() {
        return Optional.ofNullable(width);
    }

    public IntegerFilter width() {
        if (width == null) {
            setWidth(new IntegerFilter());
        }
        return width;
    }

    public void setWidth(IntegerFilter width) {
        this.width = width;
    }

    public IntegerFilter getHeight() {
        return height;
    }

    public Optional<IntegerFilter> optionalHeight() {
        return Optional.ofNullable(height);
    }

    public IntegerFilter height() {
        if (height == null) {
            setHeight(new IntegerFilter());
        }
        return height;
    }

    public void setHeight(IntegerFilter height) {
        this.height = height;
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

    public LongFilter getBranchId() {
        return branchId;
    }

    public Optional<LongFilter> optionalBranchId() {
        return Optional.ofNullable(branchId);
    }

    public LongFilter branchId() {
        if (branchId == null) {
            setBranchId(new LongFilter());
        }
        return branchId;
    }

    public void setBranchId(LongFilter branchId) {
        this.branchId = branchId;
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
        final GalleryImageCriteria that = (GalleryImageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(captionVi, that.captionVi) &&
            Objects.equals(captionEn, that.captionEn) &&
            Objects.equals(altVi, that.altVi) &&
            Objects.equals(altEn, that.altEn) &&
            Objects.equals(width, that.width) &&
            Objects.equals(height, that.height) &&
            Objects.equals(displayOrder, that.displayOrder) &&
            Objects.equals(active, that.active) &&
            Objects.equals(branchId, that.branchId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, captionVi, captionEn, altVi, altEn, width, height, displayOrder, active, branchId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GalleryImageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalCaptionVi().map(f -> "captionVi=" + f + ", ").orElse("") +
            optionalCaptionEn().map(f -> "captionEn=" + f + ", ").orElse("") +
            optionalAltVi().map(f -> "altVi=" + f + ", ").orElse("") +
            optionalAltEn().map(f -> "altEn=" + f + ", ").orElse("") +
            optionalWidth().map(f -> "width=" + f + ", ").orElse("") +
            optionalHeight().map(f -> "height=" + f + ", ").orElse("") +
            optionalDisplayOrder().map(f -> "displayOrder=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalBranchId().map(f -> "branchId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
