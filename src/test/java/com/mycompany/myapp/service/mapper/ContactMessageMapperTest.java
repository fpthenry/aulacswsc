package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ContactMessageAsserts.*;
import static com.mycompany.myapp.domain.ContactMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactMessageMapperTest {

    private ContactMessageMapper contactMessageMapper;

    @BeforeEach
    void setUp() {
        contactMessageMapper = new ContactMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getContactMessageSample1();
        var actual = contactMessageMapper.toEntity(contactMessageMapper.toDto(expected));
        assertContactMessageAllPropertiesEquals(expected, actual);
    }
}
