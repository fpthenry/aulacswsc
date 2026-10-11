package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpaServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpaServiceDTO.class);
        SpaServiceDTO spaServiceDTO1 = new SpaServiceDTO();
        spaServiceDTO1.setId(1L);
        SpaServiceDTO spaServiceDTO2 = new SpaServiceDTO();
        assertThat(spaServiceDTO1).isNotEqualTo(spaServiceDTO2);
        spaServiceDTO2.setId(spaServiceDTO1.getId());
        assertThat(spaServiceDTO1).isEqualTo(spaServiceDTO2);
        spaServiceDTO2.setId(2L);
        assertThat(spaServiceDTO1).isNotEqualTo(spaServiceDTO2);
        spaServiceDTO1.setId(null);
        assertThat(spaServiceDTO1).isNotEqualTo(spaServiceDTO2);
    }
}
