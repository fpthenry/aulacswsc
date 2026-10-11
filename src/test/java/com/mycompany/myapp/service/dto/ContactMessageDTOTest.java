package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContactMessageDTO.class);
        ContactMessageDTO contactMessageDTO1 = new ContactMessageDTO();
        contactMessageDTO1.setId(1L);
        ContactMessageDTO contactMessageDTO2 = new ContactMessageDTO();
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
        contactMessageDTO2.setId(contactMessageDTO1.getId());
        assertThat(contactMessageDTO1).isEqualTo(contactMessageDTO2);
        contactMessageDTO2.setId(2L);
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
        contactMessageDTO1.setId(null);
        assertThat(contactMessageDTO1).isNotEqualTo(contactMessageDTO2);
    }
}
