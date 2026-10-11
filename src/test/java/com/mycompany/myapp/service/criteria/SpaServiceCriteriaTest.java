package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SpaServiceCriteriaTest {

    @Test
    void newSpaServiceCriteriaHasAllFiltersNullTest() {
        var spaServiceCriteria = new SpaServiceCriteria();
        assertThat(spaServiceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void spaServiceCriteriaFluentMethodsCreatesFiltersTest() {
        var spaServiceCriteria = new SpaServiceCriteria();

        setAllFilters(spaServiceCriteria);

        assertThat(spaServiceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void spaServiceCriteriaCopyCreatesNullFilterTest() {
        var spaServiceCriteria = new SpaServiceCriteria();
        var copy = spaServiceCriteria.copy();

        assertThat(spaServiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(spaServiceCriteria)
        );
    }

    @Test
    void spaServiceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var spaServiceCriteria = new SpaServiceCriteria();
        setAllFilters(spaServiceCriteria);

        var copy = spaServiceCriteria.copy();

        assertThat(spaServiceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(spaServiceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var spaServiceCriteria = new SpaServiceCriteria();

        assertThat(spaServiceCriteria).hasToString("SpaServiceCriteria{}");
    }

    private static void setAllFilters(SpaServiceCriteria spaServiceCriteria) {
        spaServiceCriteria.id();
        spaServiceCriteria.name();
        spaServiceCriteria.slug();
        spaServiceCriteria.shortDescription();
        spaServiceCriteria.price();
        spaServiceCriteria.salePrice();
        spaServiceCriteria.durationMinutes();
        spaServiceCriteria.imageUrl();
        spaServiceCriteria.featured();
        spaServiceCriteria.active();
        spaServiceCriteria.categoryId();
        spaServiceCriteria.staffsId();
        spaServiceCriteria.distinct();
    }

    private static Condition<SpaServiceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSlug()) &&
                condition.apply(criteria.getShortDescription()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getSalePrice()) &&
                condition.apply(criteria.getDurationMinutes()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getFeatured()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getStaffsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SpaServiceCriteria> copyFiltersAre(SpaServiceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSlug(), copy.getSlug()) &&
                condition.apply(criteria.getShortDescription(), copy.getShortDescription()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getSalePrice(), copy.getSalePrice()) &&
                condition.apply(criteria.getDurationMinutes(), copy.getDurationMinutes()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getFeatured(), copy.getFeatured()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getStaffsId(), copy.getStaffsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
