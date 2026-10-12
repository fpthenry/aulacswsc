package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SiteSettingAsserts.*;
import static com.mycompany.myapp.domain.SiteSettingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SiteSettingMapperTest {

    private SiteSettingMapper siteSettingMapper;

    @BeforeEach
    void setUp() {
        siteSettingMapper = new SiteSettingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSiteSettingSample1();
        var actual = siteSettingMapper.toEntity(siteSettingMapper.toDto(expected));
        assertSiteSettingAllPropertiesEquals(expected, actual);
    }
}
