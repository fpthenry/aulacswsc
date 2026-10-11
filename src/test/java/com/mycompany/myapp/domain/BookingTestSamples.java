package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BookingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Booking getBookingSample1() {
        return new Booking().id(1L).code("code1").note("note1");
    }

    public static Booking getBookingSample2() {
        return new Booking().id(2L).code("code2").note("note2");
    }

    public static Booking getBookingRandomSampleGenerator() {
        return new Booking().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).note(UUID.randomUUID().toString());
    }
}
