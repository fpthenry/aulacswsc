package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookingItemTestSamples.*;
import static com.mycompany.myapp.domain.BookingTestSamples.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;
import static com.mycompany.myapp.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingItem.class);
        BookingItem bookingItem1 = getBookingItemSample1();
        BookingItem bookingItem2 = new BookingItem();
        assertThat(bookingItem1).isNotEqualTo(bookingItem2);

        bookingItem2.setId(bookingItem1.getId());
        assertThat(bookingItem1).isEqualTo(bookingItem2);

        bookingItem2 = getBookingItemSample2();
        assertThat(bookingItem1).isNotEqualTo(bookingItem2);
    }

    @Test
    void serviceTest() {
        BookingItem bookingItem = getBookingItemRandomSampleGenerator();
        SpaService spaServiceBack = getSpaServiceRandomSampleGenerator();

        bookingItem.setService(spaServiceBack);
        assertThat(bookingItem.getService()).isEqualTo(spaServiceBack);

        bookingItem.service(null);
        assertThat(bookingItem.getService()).isNull();
    }

    @Test
    void staffTest() {
        BookingItem bookingItem = getBookingItemRandomSampleGenerator();
        Staff staffBack = getStaffRandomSampleGenerator();

        bookingItem.setStaff(staffBack);
        assertThat(bookingItem.getStaff()).isEqualTo(staffBack);

        bookingItem.staff(null);
        assertThat(bookingItem.getStaff()).isNull();
    }

    @Test
    void bookingTest() {
        BookingItem bookingItem = getBookingItemRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        bookingItem.setBooking(bookingBack);
        assertThat(bookingItem.getBooking()).isEqualTo(bookingBack);

        bookingItem.booking(null);
        assertThat(bookingItem.getBooking()).isNull();
    }
}
