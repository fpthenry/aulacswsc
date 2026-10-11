package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SpaServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SpaService getSpaServiceSample1() {
        return new SpaService()
            .id(1L)
            .name("name1")
            .slug("slug1")
            .shortDescription("shortDescription1")
            .durationMinutes(1)
            .imageUrl("imageUrl1");
    }

    public static SpaService getSpaServiceSample2() {
        return new SpaService()
            .id(2L)
            .name("name2")
            .slug("slug2")
            .shortDescription("shortDescription2")
            .durationMinutes(2)
            .imageUrl("imageUrl2");
    }

    public static SpaService getSpaServiceRandomSampleGenerator() {
        return new SpaService()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .shortDescription(UUID.randomUUID().toString())
            .durationMinutes(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString());
    }
}
