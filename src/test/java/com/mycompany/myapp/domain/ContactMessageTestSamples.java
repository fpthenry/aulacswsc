package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactMessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ContactMessage getContactMessageSample1() {
        return new ContactMessage().id(1L).fullName("fullName1").phone("phone1").email("email1").message("message1");
    }

    public static ContactMessage getContactMessageSample2() {
        return new ContactMessage().id(2L).fullName("fullName2").phone("phone2").email("email2").message("message2");
    }

    public static ContactMessage getContactMessageRandomSampleGenerator() {
        return new ContactMessage()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString());
    }
}
