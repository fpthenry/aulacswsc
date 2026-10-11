package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ServiceCategoryTestSamples.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;
import static com.mycompany.myapp.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpaServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpaService.class);
        SpaService spaService1 = getSpaServiceSample1();
        SpaService spaService2 = new SpaService();
        assertThat(spaService1).isNotEqualTo(spaService2);

        spaService2.setId(spaService1.getId());
        assertThat(spaService1).isEqualTo(spaService2);

        spaService2 = getSpaServiceSample2();
        assertThat(spaService1).isNotEqualTo(spaService2);
    }

    @Test
    void categoryTest() {
        SpaService spaService = getSpaServiceRandomSampleGenerator();
        ServiceCategory serviceCategoryBack = getServiceCategoryRandomSampleGenerator();

        spaService.setCategory(serviceCategoryBack);
        assertThat(spaService.getCategory()).isEqualTo(serviceCategoryBack);

        spaService.category(null);
        assertThat(spaService.getCategory()).isNull();
    }

    @Test
    void staffsTest() {
        SpaService spaService = getSpaServiceRandomSampleGenerator();
        Staff staffBack = getStaffRandomSampleGenerator();

        spaService.addStaffs(staffBack);
        assertThat(spaService.getStaffs()).containsOnly(staffBack);
        assertThat(staffBack.getServices()).containsOnly(spaService);

        spaService.removeStaffs(staffBack);
        assertThat(spaService.getStaffs()).doesNotContain(staffBack);
        assertThat(staffBack.getServices()).doesNotContain(spaService);

        spaService.staffs(new HashSet<>(Set.of(staffBack)));
        assertThat(spaService.getStaffs()).containsOnly(staffBack);
        assertThat(staffBack.getServices()).containsOnly(spaService);

        spaService.setStaffs(new HashSet<>());
        assertThat(spaService.getStaffs()).doesNotContain(staffBack);
        assertThat(staffBack.getServices()).doesNotContain(spaService);
    }
}
