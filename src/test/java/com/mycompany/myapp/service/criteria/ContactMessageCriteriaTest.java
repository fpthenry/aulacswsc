package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ContactMessageCriteriaTest {

    @Test
    void newContactMessageCriteriaHasAllFiltersNullTest() {
        var contactMessageCriteria = new ContactMessageCriteria();
        assertThat(contactMessageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void contactMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var contactMessageCriteria = new ContactMessageCriteria();

        setAllFilters(contactMessageCriteria);

        assertThat(contactMessageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void contactMessageCriteriaCopyCreatesNullFilterTest() {
        var contactMessageCriteria = new ContactMessageCriteria();
        var copy = contactMessageCriteria.copy();

        assertThat(contactMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(contactMessageCriteria)
        );
    }

    @Test
    void contactMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var contactMessageCriteria = new ContactMessageCriteria();
        setAllFilters(contactMessageCriteria);

        var copy = contactMessageCriteria.copy();

        assertThat(contactMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(contactMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var contactMessageCriteria = new ContactMessageCriteria();

        assertThat(contactMessageCriteria).hasToString("ContactMessageCriteria{}");
    }

    private static void setAllFilters(ContactMessageCriteria contactMessageCriteria) {
        contactMessageCriteria.id();
        contactMessageCriteria.fullName();
        contactMessageCriteria.phone();
        contactMessageCriteria.email();
        contactMessageCriteria.message();
        contactMessageCriteria.handled();
        contactMessageCriteria.createdDate();
        contactMessageCriteria.distinct();
    }

    private static Condition<ContactMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFullName()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getHandled()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ContactMessageCriteria> copyFiltersAre(
        ContactMessageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFullName(), copy.getFullName()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getHandled(), copy.getHandled()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
