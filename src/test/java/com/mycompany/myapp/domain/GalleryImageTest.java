package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BranchTestSamples.*;
import static com.mycompany.myapp.domain.GalleryImageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GalleryImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GalleryImage.class);
        GalleryImage galleryImage1 = getGalleryImageSample1();
        GalleryImage galleryImage2 = new GalleryImage();
        assertThat(galleryImage1).isNotEqualTo(galleryImage2);

        galleryImage2.setId(galleryImage1.getId());
        assertThat(galleryImage1).isEqualTo(galleryImage2);

        galleryImage2 = getGalleryImageSample2();
        assertThat(galleryImage1).isNotEqualTo(galleryImage2);
    }

    @Test
    void branchTest() {
        GalleryImage galleryImage = getGalleryImageRandomSampleGenerator();
        Branch branchBack = getBranchRandomSampleGenerator();

        galleryImage.setBranch(branchBack);
        assertThat(galleryImage.getBranch()).isEqualTo(branchBack);

        galleryImage.branch(null);
        assertThat(galleryImage.getBranch()).isNull();
    }
}
