package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ServiceCategoryAsserts.*;
import static com.mycompany.myapp.domain.ServiceCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceCategoryMapperTest {

    private ServiceCategoryMapper serviceCategoryMapper;

    @BeforeEach
    void setUp() {
        serviceCategoryMapper = new ServiceCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceCategorySample1();
        var actual = serviceCategoryMapper.toEntity(serviceCategoryMapper.toDto(expected));
        assertServiceCategoryAllPropertiesEquals(expected, actual);
    }
}
