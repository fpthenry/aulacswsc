package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GalleryImageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static GalleryImage getGalleryImageSample1() {
        return new GalleryImage()
            .id(1L)
            .imageUrl("imageUrl1")
            .captionVi("captionVi1")
            .captionEn("captionEn1")
            .altVi("altVi1")
            .altEn("altEn1")
            .width(1)
            .height(1)
            .displayOrder(1);
    }

    public static GalleryImage getGalleryImageSample2() {
        return new GalleryImage()
            .id(2L)
            .imageUrl("imageUrl2")
            .captionVi("captionVi2")
            .captionEn("captionEn2")
            .altVi("altVi2")
            .altEn("altEn2")
            .width(2)
            .height(2)
            .displayOrder(2);
    }

    public static GalleryImage getGalleryImageRandomSampleGenerator() {
        return new GalleryImage()
            .id(longCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .captionVi(UUID.randomUUID().toString())
            .captionEn(UUID.randomUUID().toString())
            .altVi(UUID.randomUUID().toString())
            .altEn(UUID.randomUUID().toString())
            .width(intCount.incrementAndGet())
            .height(intCount.incrementAndGet())
            .displayOrder(intCount.incrementAndGet());
    }
}
