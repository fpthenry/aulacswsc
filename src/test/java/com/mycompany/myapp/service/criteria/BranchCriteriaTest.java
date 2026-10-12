package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BranchCriteriaTest {

    @Test
    void newBranchCriteriaHasAllFiltersNullTest() {
        var branchCriteria = new BranchCriteria();
        assertThat(branchCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void branchCriteriaFluentMethodsCreatesFiltersTest() {
        var branchCriteria = new BranchCriteria();

        setAllFilters(branchCriteria);

        assertThat(branchCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void branchCriteriaCopyCreatesNullFilterTest() {
        var branchCriteria = new BranchCriteria();
        var copy = branchCriteria.copy();

        assertThat(branchCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(branchCriteria)
        );
    }

    @Test
    void branchCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var branchCriteria = new BranchCriteria();
        setAllFilters(branchCriteria);

        var copy = branchCriteria.copy();

        assertThat(branchCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(branchCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var branchCriteria = new BranchCriteria();

        assertThat(branchCriteria).hasToString("BranchCriteria{}");
    }

    private static void setAllFilters(BranchCriteria branchCriteria) {
        branchCriteria.id();
        branchCriteria.nameVi();
        branchCriteria.nameEn();
        branchCriteria.slug();
        branchCriteria.addressVi();
        branchCriteria.addressEn();
        branchCriteria.phone();
        branchCriteria.whatsapp();
        branchCriteria.zalo();
        branchCriteria.email();
        branchCriteria.openTime();
        branchCriteria.closeTime();
        branchCriteria.lastBookingTime();
        branchCriteria.capacity();
        branchCriteria.mapEmbedUrl();
        branchCriteria.mapDirectionUrl();
        branchCriteria.imageUrl();
        branchCriteria.displayOrder();
        branchCriteria.active();
        branchCriteria.distinct();
    }

    private static Condition<BranchCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNameVi()) &&
                condition.apply(criteria.getNameEn()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getAddressVi()) &&
                condition.apply(criteria.getAddressEn()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getWhatsapp()) &&
                condition.apply(criteria.getZalo()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getOpenTime()) &&
                condition.apply(criteria.getCloseTime()) &&
                condition.apply(criteria.getLastBookingTime()) &&
                condition.apply(criteria.getCapacity()) &&
                condition.apply(criteria.getMapEmbedUrl()) &&
                condition.apply(criteria.getMapDirectionUrl()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BranchCriteria> copyFiltersAre(BranchCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNameVi(), copy.getNameVi()) &&
                condition.apply(criteria.getNameEn(), copy.getNameEn()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getAddressVi(), copy.getAddressVi()) &&
                condition.apply(criteria.getAddressEn(), copy.getAddressEn()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getWhatsapp(), copy.getWhatsapp()) &&
                condition.apply(criteria.getZalo(), copy.getZalo()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getOpenTime(), copy.getOpenTime()) &&
                condition.apply(criteria.getCloseTime(), copy.getCloseTime()) &&
                condition.apply(criteria.getLastBookingTime(), copy.getLastBookingTime()) &&
                condition.apply(criteria.getCapacity(), copy.getCapacity()) &&
                condition.apply(criteria.getMapEmbedUrl(), copy.getMapEmbedUrl()) &&
                condition.apply(criteria.getMapDirectionUrl(), copy.getMapDirectionUrl()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder(), copy.getDisplayOrder()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
