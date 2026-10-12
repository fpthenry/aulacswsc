package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ServiceCategoryCriteriaTest {

    @Test
    void newServiceCategoryCriteriaHasAllFiltersNullTest() {
        var serviceCategoryCriteria = new ServiceCategoryCriteria();
        assertThat(serviceCategoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void serviceCategoryCriteriaFluentMethodsCreatesFiltersTest() {
        var serviceCategoryCriteria = new ServiceCategoryCriteria();

        setAllFilters(serviceCategoryCriteria);

        assertThat(serviceCategoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void serviceCategoryCriteriaCopyCreatesNullFilterTest() {
        var serviceCategoryCriteria = new ServiceCategoryCriteria();
        var copy = serviceCategoryCriteria.copy();

        assertThat(serviceCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(serviceCategoryCriteria)
        );
    }

    @Test
    void serviceCategoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var serviceCategoryCriteria = new ServiceCategoryCriteria();
        setAllFilters(serviceCategoryCriteria);

        var copy = serviceCategoryCriteria.copy();

        assertThat(serviceCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(serviceCategoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var serviceCategoryCriteria = new ServiceCategoryCriteria();

        assertThat(serviceCategoryCriteria).hasToString("ServiceCategoryCriteria{}");
    }

    private static void setAllFilters(ServiceCategoryCriteria serviceCategoryCriteria) {
        serviceCategoryCriteria.id();
        serviceCategoryCriteria.nameVi();
        serviceCategoryCriteria.nameEn();
        serviceCategoryCriteria.slug();
        serviceCategoryCriteria.descriptionVi();
        serviceCategoryCriteria.descriptionEn();
        serviceCategoryCriteria.imageUrl();
        serviceCategoryCriteria.displayOrder();
        serviceCategoryCriteria.active();
        serviceCategoryCriteria.servicesId();
        serviceCategoryCriteria.distinct();
    }

    private static Condition<ServiceCategoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNameVi()) &&
                condition.apply(criteria.getNameEn()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getDescriptionVi()) &&
                condition.apply(criteria.getDescriptionEn()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getServicesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ServiceCategoryCriteria> copyFiltersAre(
        ServiceCategoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNameVi(), copy.getNameVi()) &&
                condition.apply(criteria.getNameEn(), copy.getNameEn()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getDescriptionVi(), copy.getDescriptionVi()) &&
                condition.apply(criteria.getDescriptionEn(), copy.getDescriptionEn()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getDisplayOrder(), copy.getDisplayOrder()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getServicesId(), copy.getServicesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
