package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SpaServiceAsserts.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpaServiceMapperTest {

    private SpaServiceMapper spaServiceMapper;

    @BeforeEach
    void setUp() {
        spaServiceMapper = new SpaServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSpaServiceSample1();
        var actual = spaServiceMapper.toEntity(spaServiceMapper.toDto(expected));
        assertSpaServiceAllPropertiesEquals(expected, actual);
    }
}
