package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BranchTestSamples.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;
import static com.mycompany.myapp.domain.StaffTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StaffTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Staff.class);
        Staff staff1 = getStaffSample1();
        Staff staff2 = new Staff();
        assertThat(staff1).isNotEqualTo(staff2);

        staff2.setId(staff1.getId());
        assertThat(staff1).isEqualTo(staff2);

        staff2 = getStaffSample2();
        assertThat(staff1).isNotEqualTo(staff2);
    }

    @Test
    void branchTest() {
        Staff staff = getStaffRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        staff.setBranch(branchBack);
        assertThat(staff.getBranch()).isEqualTo(branchBack);

        staff.branch(null);
        assertThat(staff.getBranch()).isNull();
    }

    @Test
    void servicesTest() {
        Staff staff = getStaffRandomSampleGenerator();
        SpaService spaServiceBack = getSpaServiceRandomSampleGenerator();

        staff.addServices(spaServiceBack);
        assertThat(staff.getServices()).containsOnly(spaServiceBack);

        staff.removeServices(spaServiceBack);
        assertThat(staff.getServices()).doesNotContain(spaServiceBack);

        staff.services(new HashSet<>(Set.of(spaServiceBack)));
        assertThat(staff.getServices()).containsOnly(spaServiceBack);

        staff.setServices(new HashSet<>());
        assertThat(staff.getServices()).doesNotContain(spaServiceBack);
    }
}
