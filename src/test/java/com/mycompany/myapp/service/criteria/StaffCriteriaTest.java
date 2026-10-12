package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StaffCriteriaTest {

    @Test
    void newStaffCriteriaHasAllFiltersNullTest() {
        var staffCriteria = new StaffCriteria();
        assertThat(staffCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void staffCriteriaFluentMethodsCreatesFiltersTest() {
        var staffCriteria = new StaffCriteria();

        setAllFilters(staffCriteria);

        assertThat(staffCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void staffCriteriaCopyCreatesNullFilterTest() {
        var staffCriteria = new StaffCriteria();
        var copy = staffCriteria.copy();

        assertThat(staffCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(staffCriteria)
        );
    }

    @Test
    void staffCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var staffCriteria = new StaffCriteria();
        setAllFilters(staffCriteria);

        var copy = staffCriteria.copy();

        assertThat(staffCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(staffCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var staffCriteria = new StaffCriteria();

        assertThat(staffCriteria).hasToString("StaffCriteria{}");
    }

    private static void setAllFilters(StaffCriteria staffCriteria) {
        staffCriteria.id();
        staffCriteria.fullName();
        staffCriteria.phone();
        staffCriteria.position();
        staffCriteria.avatarUrl();
        staffCriteria.active();
        staffCriteria.userId();
        staffCriteria.branchId();
        staffCriteria.servicesId();
        staffCriteria.distinct();
    }

    private static Condition<StaffCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getPosition()) &&
                condition.apply(criteria.getAvatarUrl()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getBranchId()) &&
                condition.apply(criteria.getServicesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StaffCriteria> copyFiltersAre(StaffCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getPosition(), copy.getPosition()) &&
                condition.apply(criteria.getAvatarUrl(), copy.getAvatarUrl()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getBranchId(), copy.getBranchId()) &&
                condition.apply(criteria.getServicesId(), copy.getServicesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
