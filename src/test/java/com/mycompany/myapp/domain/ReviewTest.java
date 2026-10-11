package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CustomerTestSamples.*;
import static com.mycompany.myapp.domain.ReviewTestSamples.*;
import static com.mycompany.myapp.domain.SpaServiceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void customerTest() {
        Review review = getReviewRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        review.setCustomer(customerBack);
        assertThat(review.getCustomer()).isEqualTo(customerBack);

        review.customer(null);
        assertThat(review.getCustomer()).isNull();
    }

    @Test
    void serviceTest() {
        Review review = getReviewRandomSampleGenerator();
        SpaService spaServiceBack = getSpaServiceRandomSampleGenerator();

        review.setService(spaServiceBack);
        assertThat(review.getService()).isEqualTo(spaServiceBack);

        review.service(null);
        assertThat(review.getService()).isNull();
    }
}
