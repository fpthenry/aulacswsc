package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BranchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Branch getBranchSample1() {
        return new Branch()
            .id(1L)
            .nameVi("nameVi1")
            .nameEn("nameEn1")
            .slug("slug1")
            .addressVi("addressVi1")
            .addressEn("addressEn1")
            .phone("phone1")
            .whatsapp("whatsapp1")
            .zalo("zalo1")
            .email("email1")
            .openTime("openTime1")
            .closeTime("closeTime1")
            .lastBookingTime("lastBookingTime1")
            .capacity(1)
            .mapEmbedUrl("mapEmbedUrl1")
            .mapDirectionUrl("mapDirectionUrl1")
            .imageUrl("imageUrl1")
            .displayOrder(1);
    }

    public static Branch getBranchSample2() {
        return new Branch()
            .id(2L)
            .nameVi("nameVi2")
            .nameEn("nameEn2")
            .slug("slug2")
            .addressVi("addressVi2")
            .addressEn("addressEn2")
            .phone("phone2")
            .whatsapp("whatsapp2")
            .zalo("zalo2")
            .email("email2")
            .openTime("openTime2")
            .closeTime("closeTime2")
            .lastBookingTime("lastBookingTime2")
            .capacity(2)
            .mapEmbedUrl("mapEmbedUrl2")
            .mapDirectionUrl("mapDirectionUrl2")
            .imageUrl("imageUrl2")
            .displayOrder(2);
    }

    public static Branch getBranchRandomSampleGenerator() {
        return new Branch()
            .id(longCount.incrementAndGet())
            .nameVi(UUID.randomUUID().toString())
            .nameEn(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .addressVi(UUID.randomUUID().toString())
            .addressEn(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .whatsapp(UUID.randomUUID().toString())
            .zalo(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .openTime(UUID.randomUUID().toString())
            .closeTime(UUID.randomUUID().toString())
            .lastBookingTime(UUID.randomUUID().toString())
            .capacity(intCount.incrementAndGet())
            .mapEmbedUrl(UUID.randomUUID().toString())
            .mapDirectionUrl(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .displayOrder(intCount.incrementAndGet());
    }
}
