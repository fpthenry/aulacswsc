package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BlogPostTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BlogPost getBlogPostSample1() {
        return new BlogPost()
            .id(1L)
            .titleVi("titleVi1")
            .titleEn("titleEn1")
            .slug("slug1")
            .excerptVi("excerptVi1")
            .excerptEn("excerptEn1")
            .coverImageUrl("coverImageUrl1");
    }

    public static BlogPost getBlogPostSample2() {
        return new BlogPost()
            .id(2L)
            .titleVi("titleVi2")
            .titleEn("titleEn2")
            .slug("slug2")
            .excerptVi("excerptVi2")
            .excerptEn("excerptEn2")
            .coverImageUrl("coverImageUrl2");
    }

    public static BlogPost getBlogPostRandomSampleGenerator() {
        return new BlogPost()
            .id(longCount.incrementAndGet())
            .titleVi(UUID.randomUUID().toString())
            .titleEn(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .excerptVi(UUID.randomUUID().toString())
            .excerptEn(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString());
    }
}
