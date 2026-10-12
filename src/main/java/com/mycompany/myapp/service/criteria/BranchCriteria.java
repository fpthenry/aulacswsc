package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Branch} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.BranchResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /branches?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BranchCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nameVi;

    private StringFilter nameEn;

    private StringFilter slug;

    private StringFilter addressVi;

    private StringFilter addressEn;

    private StringFilter phone;

    private StringFilter whatsapp;

    private StringFilter zalo;

    private StringFilter email;

    private StringFilter openTime;

    private StringFilter closeTime;

    private StringFilter lastBookingTime;

    private IntegerFilter capacity;

    private StringFilter mapEmbedUrl;

    private StringFilter mapDirectionUrl;

    private StringFilter imageUrl;

    private IntegerFilter displayOrder;

    private BooleanFilter active;

    private Boolean distinct;

    public BranchCriteria() {}

    public BranchCriteria(BranchCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nameVi = other.optionalNameVi().map(StringFilter::copy).orElse(null);
        this.nameEn = other.optionalNameEn().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.addressVi = other.optionalAddressVi().map(StringFilter::copy).orElse(null);
        this.addressEn = other.optionalAddressEn().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.whatsapp = other.optionalWhatsapp().map(StringFilter::copy).orElse(null);
        this.zalo = other.optionalZalo().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.openTime = other.optionalOpenTime().map(StringFilter::copy).orElse(null);
        this.closeTime = other.optionalCloseTime().map(StringFilter::copy).orElse(null);
        this.lastBookingTime = other.optionalLastBookingTime().map(StringFilter::copy).orElse(null);
        this.capacity = other.optionalCapacity().map(IntegerFilter::copy).orElse(null);
        this.mapEmbedUrl = other.optionalMapEmbedUrl().map(StringFilter::copy).orElse(null);
        this.mapDirectionUrl = other.optionalMapDirectionUrl().map(StringFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.displayOrder = other.optionalDisplayOrder().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BranchCriteria copy() {
        return new BranchCriteria(this);
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

    public StringFilter getAddressVi() {
        return addressVi;
    }

    public Optional<StringFilter> optionalAddressVi() {
        return Optional.ofNullable(addressVi);
    }

    public StringFilter addressVi() {
        if (addressVi == null) {
            setAddressVi(new StringFilter());
        }
        return addressVi;
    }

    public void setAddressVi(StringFilter addressVi) {
        this.addressVi = addressVi;
    }

    public StringFilter getAddressEn() {
        return addressEn;
    }

    public Optional<StringFilter> optionalAddressEn() {
        return Optional.ofNullable(addressEn);
    }

    public StringFilter addressEn() {
        if (addressEn == null) {
            setAddressEn(new StringFilter());
        }
        return addressEn;
    }

    public void setAddressEn(StringFilter addressEn) {
        this.addressEn = addressEn;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getWhatsapp() {
        return whatsapp;
    }

    public Optional<StringFilter> optionalWhatsapp() {
        return Optional.ofNullable(whatsapp);
    }

    public StringFilter whatsapp() {
        if (whatsapp == null) {
            setWhatsapp(new StringFilter());
        }
        return whatsapp;
    }

    public void setWhatsapp(StringFilter whatsapp) {
        this.whatsapp = whatsapp;
    }

    public StringFilter getZalo() {
        return zalo;
    }

    public Optional<StringFilter> optionalZalo() {
        return Optional.ofNullable(zalo);
    }

    public StringFilter zalo() {
        if (zalo == null) {
            setZalo(new StringFilter());
        }
        return zalo;
    }

    public void setZalo(StringFilter zalo) {
        this.zalo = zalo;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getOpenTime() {
        return openTime;
    }

    public Optional<StringFilter> optionalOpenTime() {
        return Optional.ofNullable(openTime);
    }

    public StringFilter openTime() {
        if (openTime == null) {
            setOpenTime(new StringFilter());
        }
        return openTime;
    }

    public void setOpenTime(StringFilter openTime) {
        this.openTime = openTime;
    }

    public StringFilter getCloseTime() {
        return closeTime;
    }

    public Optional<StringFilter> optionalCloseTime() {
        return Optional.ofNullable(closeTime);
    }

    public StringFilter closeTime() {
        if (closeTime == null) {
            setCloseTime(new StringFilter());
        }
        return closeTime;
    }

    public void setCloseTime(StringFilter closeTime) {
        this.closeTime = closeTime;
    }

    public StringFilter getLastBookingTime() {
        return lastBookingTime;
    }

    public Optional<StringFilter> optionalLastBookingTime() {
        return Optional.ofNullable(lastBookingTime);
    }

    public StringFilter lastBookingTime() {
        if (lastBookingTime == null) {
            setLastBookingTime(new StringFilter());
        }
        return lastBookingTime;
    }

    public void setLastBookingTime(StringFilter lastBookingTime) {
        this.lastBookingTime = lastBookingTime;
    }

    public IntegerFilter getCapacity() {
        return capacity;
    }

    public Optional<IntegerFilter> optionalCapacity() {
        return Optional.ofNullable(capacity);
    }

    public IntegerFilter capacity() {
        if (capacity == null) {
            setCapacity(new IntegerFilter());
        }
        return capacity;
    }

    public void setCapacity(IntegerFilter capacity) {
        this.capacity = capacity;
    }

    public StringFilter getMapEmbedUrl() {
        return mapEmbedUrl;
    }

    public Optional<StringFilter> optionalMapEmbedUrl() {
        return Optional.ofNullable(mapEmbedUrl);
    }

    public StringFilter mapEmbedUrl() {
        if (mapEmbedUrl == null) {
            setMapEmbedUrl(new StringFilter());
        }
        return mapEmbedUrl;
    }

    public void setMapEmbedUrl(StringFilter mapEmbedUrl) {
        this.mapEmbedUrl = mapEmbedUrl;
    }

    public StringFilter getMapDirectionUrl() {
        return mapDirectionUrl;
    }

    public Optional<StringFilter> optionalMapDirectionUrl() {
        return Optional.ofNullable(mapDirectionUrl);
    }

    public StringFilter mapDirectionUrl() {
        if (mapDirectionUrl == null) {
            setMapDirectionUrl(new StringFilter());
        }
        return mapDirectionUrl;
    }

    public void setMapDirectionUrl(StringFilter mapDirectionUrl) {
        this.mapDirectionUrl = mapDirectionUrl;
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
        final BranchCriteria that = (BranchCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nameVi, that.nameVi) &&
            Objects.equals(nameEn, that.nameEn) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(addressVi, that.addressVi) &&
            Objects.equals(addressEn, that.addressEn) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(whatsapp, that.whatsapp) &&
            Objects.equals(zalo, that.zalo) &&
            Objects.equals(email, that.email) &&
            Objects.equals(openTime, that.openTime) &&
            Objects.equals(closeTime, that.closeTime) &&
            Objects.equals(lastBookingTime, that.lastBookingTime) &&
            Objects.equals(capacity, that.capacity) &&
            Objects.equals(mapEmbedUrl, that.mapEmbedUrl) &&
            Objects.equals(mapDirectionUrl, that.mapDirectionUrl) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(displayOrder, that.displayOrder) &&
            Objects.equals(active, that.active) &&
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
            addressVi,
            addressEn,
            phone,
            whatsapp,
            zalo,
            email,
            openTime,
            closeTime,
            lastBookingTime,
            capacity,
            mapEmbedUrl,
            mapDirectionUrl,
            imageUrl,
            displayOrder,
            active,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BranchCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNameVi().map(f -> "nameVi=" + f + ", ").orElse("") +
            optionalNameEn().map(f -> "nameEn=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalAddressVi().map(f -> "addressVi=" + f + ", ").orElse("") +
            optionalAddressEn().map(f -> "addressEn=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalWhatsapp().map(f -> "whatsapp=" + f + ", ").orElse("") +
            optionalZalo().map(f -> "zalo=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalOpenTime().map(f -> "openTime=" + f + ", ").orElse("") +
            optionalCloseTime().map(f -> "closeTime=" + f + ", ").orElse("") +
            optionalLastBookingTime().map(f -> "lastBookingTime=" + f + ", ").orElse("") +
            optionalCapacity().map(f -> "capacity=" + f + ", ").orElse("") +
            optionalMapEmbedUrl().map(f -> "mapEmbedUrl=" + f + ", ").orElse("") +
            optionalMapDirectionUrl().map(f -> "mapDirectionUrl=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalDisplayOrder().map(f -> "displayOrder=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
