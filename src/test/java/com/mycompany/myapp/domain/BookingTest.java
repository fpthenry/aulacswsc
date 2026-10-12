package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookingItemTestSamples.*;
import static com.mycompany.myapp.domain.BookingTestSamples.*;
import static com.mycompany.myapp.domain.BranchTestSamples.*;
import static com.mycompany.myapp.domain.CustomerTestSamples.*;
import static com.mycompany.myapp.domain.PromotionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booking.class);
        Booking booking1 = getBookingSample1();
        Booking booking2 = new Booking();
        assertThat(booking1).isNotEqualTo(booking2);

        booking2.setId(booking1.getId());
        assertThat(booking1).isEqualTo(booking2);

        booking2 = getBookingSample2();
        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void itemsTest() {
        Booking booking = getBookingRandomSampleGenerator();
        BookingItem bookingItemBack = getBookingItemRandomSampleGenerator();

        booking.addItems(bookingItemBack);
        assertThat(booking.getItems()).containsOnly(bookingItemBack);
        assertThat(bookingItemBack.getBooking()).isEqualTo(booking);

        booking.removeItems(bookingItemBack);
        assertThat(booking.getItems()).doesNotContain(bookingItemBack);
        assertThat(bookingItemBack.getBooking()).isNull();

        booking.items(new HashSet<>(Set.of(bookingItemBack)));
        assertThat(booking.getItems()).containsOnly(bookingItemBack);
        assertThat(bookingItemBack.getBooking()).isEqualTo(booking);

        booking.setItems(new HashSet<>());
        assertThat(booking.getItems()).doesNotContain(bookingItemBack);
        assertThat(bookingItemBack.getBooking()).isNull();
    }

    @Test
    void branchTest() {
        Booking booking = getBookingRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        booking.setBranch(branchBack);
        assertThat(booking.getBranch()).isEqualTo(branchBack);

        booking.branch(null);
        assertThat(booking.getBranch()).isNull();
    }

    @Test
    void promotionTest() {
        Booking booking = getBookingRandomSampleGenerator();
        Promotion promotionBack = getPromotionRandomSampleGenerator();

        booking.setPromotion(promotionBack);
        assertThat(booking.getPromotion()).isEqualTo(promotionBack);

        booking.promotion(null);
        assertThat(booking.getPromotion()).isNull();
    }

    @Test
    void customerTest() {
        Booking booking = getBookingRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        booking.setCustomer(customerBack);
        assertThat(booking.getCustomer()).isEqualTo(customerBack);

        booking.customer(null);
        assertThat(booking.getCustomer()).isNull();
    }
}
