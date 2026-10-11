package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ServiceCategoryTestSamples.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServiceCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceCategory.class);
        ServiceCategory serviceCategory1 = getServiceCategorySample1();
        ServiceCategory serviceCategory2 = new ServiceCategory();
        assertThat(serviceCategory1).isNotEqualTo(serviceCategory2);

        serviceCategory2.setId(serviceCategory1.getId());
        assertThat(serviceCategory1).isEqualTo(serviceCategory2);

        serviceCategory2 = getServiceCategorySample2();
        assertThat(serviceCategory1).isNotEqualTo(serviceCategory2);
    }

    @Test
    void servicesTest() {
        ServiceCategory serviceCategory = getServiceCategoryRandomSampleGenerator();
        SpaService spaServiceBack = getSpaServiceRandomSampleGenerator();

        serviceCategory.addServices(spaServiceBack);
        assertThat(serviceCategory.getServices()).containsOnly(spaServiceBack);
        assertThat(spaServiceBack.getCategory()).isEqualTo(serviceCategory);

        serviceCategory.removeServices(spaServiceBack);
        assertThat(serviceCategory.getServices()).doesNotContain(spaServiceBack);
        assertThat(spaServiceBack.getCategory()).isNull();

        serviceCategory.services(new HashSet<>(Set.of(spaServiceBack)));
        assertThat(serviceCategory.getServices()).containsOnly(spaServiceBack);
        assertThat(spaServiceBack.getCategory()).isEqualTo(serviceCategory);

        serviceCategory.setServices(new HashSet<>());
        assertThat(serviceCategory.getServices()).doesNotContain(spaServiceBack);
        assertThat(spaServiceBack.getCategory()).isNull();
    }
}
