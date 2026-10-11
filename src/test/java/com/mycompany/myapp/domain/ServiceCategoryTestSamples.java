package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ServiceCategory getServiceCategorySample1() {
        return new ServiceCategory().id(1L).name("name1").slug("slug1").imageUrl("imageUrl1").displayOrder(1);
    }

    public static ServiceCategory getServiceCategorySample2() {
        return new ServiceCategory().id(2L).name("name2").slug("slug2").imageUrl("imageUrl2").displayOrder(2);
    }

    public static ServiceCategory getServiceCategoryRandomSampleGenerator() {
        return new ServiceCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
