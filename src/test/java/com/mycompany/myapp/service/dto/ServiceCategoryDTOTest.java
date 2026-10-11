package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceCategoryDTO.class);
        ServiceCategoryDTO serviceCategoryDTO1 = new ServiceCategoryDTO();
        serviceCategoryDTO1.setId(1L);
        ServiceCategoryDTO serviceCategoryDTO2 = new ServiceCategoryDTO();
        assertThat(serviceCategoryDTO1).isNotEqualTo(serviceCategoryDTO2);
        serviceCategoryDTO2.setId(serviceCategoryDTO1.getId());
        assertThat(serviceCategoryDTO1).isEqualTo(serviceCategoryDTO2);
        serviceCategoryDTO2.setId(2L);
        assertThat(serviceCategoryDTO1).isNotEqualTo(serviceCategoryDTO2);
        serviceCategoryDTO1.setId(null);
        assertThat(serviceCategoryDTO1).isNotEqualTo(serviceCategoryDTO2);
    }
}
