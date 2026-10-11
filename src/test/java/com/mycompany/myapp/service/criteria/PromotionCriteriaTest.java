package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PromotionCriteriaTest {

    @Test
    void newPromotionCriteriaHasAllFiltersNullTest() {
        var promotionCriteria = new PromotionCriteria();
        assertThat(promotionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void promotionCriteriaFluentMethodsCreatesFiltersTest() {
        var promotionCriteria = new PromotionCriteria();

        setAllFilters(promotionCriteria);

        assertThat(promotionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void promotionCriteriaCopyCreatesNullFilterTest() {
        var promotionCriteria = new PromotionCriteria();
        var copy = promotionCriteria.copy();

        assertThat(promotionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(promotionCriteria)
        );
    }

    @Test
    void promotionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var promotionCriteria = new PromotionCriteria();
        setAllFilters(promotionCriteria);

        var copy = promotionCriteria.copy();

        assertThat(promotionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(promotionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var promotionCriteria = new PromotionCriteria();

        assertThat(promotionCriteria).hasToString("PromotionCriteria{}");
    }

    private static void setAllFilters(PromotionCriteria promotionCriteria) {
        promotionCriteria.id();
        promotionCriteria.code();
        promotionCriteria.name();
        promotionCriteria.description();
        promotionCriteria.discountPercent();
        promotionCriteria.discountAmount();
        promotionCriteria.startDate();
        promotionCriteria.endDate();
        promotionCriteria.active();
        promotionCriteria.distinct();
    }

    private static Condition<PromotionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDiscountPercent()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PromotionCriteria> copyFiltersAre(PromotionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDiscountPercent(), copy.getDiscountPercent()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
