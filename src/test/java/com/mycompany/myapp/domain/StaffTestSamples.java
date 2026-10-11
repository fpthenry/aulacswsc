package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StaffTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Staff getStaffSample1() {
        return new Staff().id(1L).fullName("fullName1").phone("phone1").position("position1").avatarUrl("avatarUrl1");
    }

    public static Staff getStaffSample2() {
        return new Staff().id(2L).fullName("fullName2").phone("phone2").position("position2").avatarUrl("avatarUrl2");
    }

    public static Staff getStaffRandomSampleGenerator() {
        return new Staff()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .position(UUID.randomUUID().toString())
            .avatarUrl(UUID.randomUUID().toString());
    }
}
