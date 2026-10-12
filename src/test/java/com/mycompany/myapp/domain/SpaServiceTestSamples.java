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
            .nameVi("nameVi1")
            .nameEn("nameEn1")
            .slug("slug1")
            .shortDescriptionVi("shortDescriptionVi1")
            .shortDescriptionEn("shortDescriptionEn1")
            .durationMinutes(1)
            .imageUrl("imageUrl1")
            .displayOrder(1);
    }

    public static SpaService getSpaServiceSample2() {
        return new SpaService()
            .id(2L)
            .nameVi("nameVi2")
            .nameEn("nameEn2")
            .slug("slug2")
            .shortDescriptionVi("shortDescriptionVi2")
            .shortDescriptionEn("shortDescriptionEn2")
            .durationMinutes(2)
            .imageUrl("imageUrl2")
            .displayOrder(2);
    }

    public static SpaService getSpaServiceRandomSampleGenerator() {
        return new SpaService()
            .id(longCount.incrementAndGet())
            .nameVi(UUID.randomUUID().toString())
            .nameEn(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .shortDescriptionVi(UUID.randomUUID().toString())
            .shortDescriptionEn(UUID.randomUUID().toString())
            .durationMinutes(intCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
