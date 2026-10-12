package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.BranchAsserts.*;
import static com.mycompany.myapp.domain.BranchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BranchMapperTest {

    private BranchMapper branchMapper;

    @BeforeEach
    void setUp() {
        branchMapper = new BranchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBranchSample1();
        var actual = branchMapper.toEntity(branchMapper.toDto(expected));
        assertBranchAllPropertiesEquals(expected, actual);
    }
}
