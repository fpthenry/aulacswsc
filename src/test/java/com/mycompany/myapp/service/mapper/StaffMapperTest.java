package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.StaffAsserts.*;
import static com.mycompany.myapp.domain.StaffTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaffMapperTest {

    private StaffMapper staffMapper;

    @BeforeEach
    void setUp() {
        staffMapper = new StaffMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStaffSample1();
        var actual = staffMapper.toEntity(staffMapper.toDto(expected));
        assertStaffAllPropertiesEquals(expected, actual);
    }
}
