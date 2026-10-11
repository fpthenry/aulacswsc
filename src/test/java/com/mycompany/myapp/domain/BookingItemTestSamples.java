package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookingItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookingItem getBookingItemSample1() {
        return new BookingItem().id(1L).quantity(1).durationMinutes(1);
    }

    public static BookingItem getBookingItemSample2() {
        return new BookingItem().id(2L).quantity(2).durationMinutes(2);
    }

    public static BookingItem getBookingItemRandomSampleGenerator() {
        return new BookingItem()
            .id(longCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .durationMinutes(intCount.incrementAndGet());
    }
}
