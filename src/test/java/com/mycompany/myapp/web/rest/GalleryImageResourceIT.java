package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.GalleryImageAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.domain.GalleryImage;
import com.mycompany.myapp.repository.GalleryImageRepository;
import com.mycompany.myapp.service.GalleryImageService;
import com.mycompany.myapp.service.dto.GalleryImageDTO;
import com.mycompany.myapp.service.mapper.GalleryImageMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GalleryImageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GalleryImageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CAPTION_VI = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION_VI = "BBBBBBBBBB";

    private static final String DEFAULT_CAPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_ALT_VI = "AAAAAAAAAA";
    private static final String UPDATED_ALT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_ALT_EN = "AAAAAAAAAA";
    private static final String UPDATED_ALT_EN = "BBBBBBBBBB";

    private static final Integer DEFAULT_WIDTH = 1;
    private static final Integer UPDATED_WIDTH = 2;
    private static final Integer SMALLER_WIDTH = 1 - 1;

    private static final Integer DEFAULT_HEIGHT = 1;
    private static final Integer UPDATED_HEIGHT = 2;
    private static final Integer SMALLER_HEIGHT = 1 - 1;

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;
    private static final Integer SMALLER_DISPLAY_ORDER = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/gallery-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GalleryImageRepository galleryImageRepository;

    @Mock
    private GalleryImageRepository galleryImageRepositoryMock;

    @Autowired
    private GalleryImageMapper galleryImageMapper;

    @Mock
    private GalleryImageService galleryImageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGalleryImageMockMvc;

    private GalleryImage galleryImage;

    private GalleryImage insertedGalleryImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GalleryImage createEntity() {
        return new GalleryImage()
            .imageUrl(DEFAULT_IMAGE_URL)
            .captionVi(DEFAULT_CAPTION_VI)
            .captionEn(DEFAULT_CAPTION_EN)
            .altVi(DEFAULT_ALT_VI)
            .altEn(DEFAULT_ALT_EN)
            .width(DEFAULT_WIDTH)
            .height(DEFAULT_HEIGHT)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GalleryImage createUpdatedEntity() {
        return new GalleryImage()
            .imageUrl(UPDATED_IMAGE_URL)
            .captionVi(UPDATED_CAPTION_VI)
            .captionEn(UPDATED_CAPTION_EN)
            .altVi(UPDATED_ALT_VI)
            .altEn(UPDATED_ALT_EN)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        galleryImage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGalleryImage != null) {
            galleryImageRepository.delete(insertedGalleryImage);
            insertedGalleryImage = null;
        }
    }

    @Test
    @Transactional
    void createGalleryImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);
        var returnedGalleryImageDTO = om.readValue(
            restGalleryImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GalleryImageDTO.class
        );

        // Validate the GalleryImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGalleryImage = galleryImageMapper.toEntity(returnedGalleryImageDTO);
        assertGalleryImageUpdatableFieldsEquals(returnedGalleryImage, getPersistedGalleryImage(returnedGalleryImage));

        insertedGalleryImage = returnedGalleryImage;
    }

    @Test
    @Transactional
    void createGalleryImageWithExistingId() throws Exception {
        // Create the GalleryImage with an existing ID
        galleryImage.setId(1L);
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGalleryImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        galleryImage.setImageUrl(null);

        // Create the GalleryImage, which fails.
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        restGalleryImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAltViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        galleryImage.setAltVi(null);

        // Create the GalleryImage, which fails.
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        restGalleryImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        galleryImage.setActive(null);

        // Create the GalleryImage, which fails.
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        restGalleryImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGalleryImages() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(galleryImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].captionVi").value(hasItem(DEFAULT_CAPTION_VI)))
            .andExpect(jsonPath("$.[*].captionEn").value(hasItem(DEFAULT_CAPTION_EN)))
            .andExpect(jsonPath("$.[*].altVi").value(hasItem(DEFAULT_ALT_VI)))
            .andExpect(jsonPath("$.[*].altEn").value(hasItem(DEFAULT_ALT_EN)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGalleryImagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(galleryImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGalleryImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(galleryImageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGalleryImagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(galleryImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGalleryImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(galleryImageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getGalleryImage() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get the galleryImage
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL_ID, galleryImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(galleryImage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.captionVi").value(DEFAULT_CAPTION_VI))
            .andExpect(jsonPath("$.captionEn").value(DEFAULT_CAPTION_EN))
            .andExpect(jsonPath("$.altVi").value(DEFAULT_ALT_VI))
            .andExpect(jsonPath("$.altEn").value(DEFAULT_ALT_EN))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getGalleryImagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        Long id = galleryImage.getId();

        defaultGalleryImageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultGalleryImageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultGalleryImageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where imageUrl equals to
        defaultGalleryImageFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where imageUrl in
        defaultGalleryImageFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where imageUrl is not null
        defaultGalleryImageFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where imageUrl contains
        defaultGalleryImageFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where imageUrl does not contain
        defaultGalleryImageFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionVi equals to
        defaultGalleryImageFiltering("captionVi.equals=" + DEFAULT_CAPTION_VI, "captionVi.equals=" + UPDATED_CAPTION_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionVi in
        defaultGalleryImageFiltering("captionVi.in=" + DEFAULT_CAPTION_VI + "," + UPDATED_CAPTION_VI, "captionVi.in=" + UPDATED_CAPTION_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionVi is not null
        defaultGalleryImageFiltering("captionVi.specified=true", "captionVi.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionViContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionVi contains
        defaultGalleryImageFiltering("captionVi.contains=" + DEFAULT_CAPTION_VI, "captionVi.contains=" + UPDATED_CAPTION_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionVi does not contain
        defaultGalleryImageFiltering("captionVi.doesNotContain=" + UPDATED_CAPTION_VI, "captionVi.doesNotContain=" + DEFAULT_CAPTION_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionEn equals to
        defaultGalleryImageFiltering("captionEn.equals=" + DEFAULT_CAPTION_EN, "captionEn.equals=" + UPDATED_CAPTION_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionEn in
        defaultGalleryImageFiltering("captionEn.in=" + DEFAULT_CAPTION_EN + "," + UPDATED_CAPTION_EN, "captionEn.in=" + UPDATED_CAPTION_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionEn is not null
        defaultGalleryImageFiltering("captionEn.specified=true", "captionEn.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionEnContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionEn contains
        defaultGalleryImageFiltering("captionEn.contains=" + DEFAULT_CAPTION_EN, "captionEn.contains=" + UPDATED_CAPTION_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByCaptionEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where captionEn does not contain
        defaultGalleryImageFiltering("captionEn.doesNotContain=" + UPDATED_CAPTION_EN, "captionEn.doesNotContain=" + DEFAULT_CAPTION_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altVi equals to
        defaultGalleryImageFiltering("altVi.equals=" + DEFAULT_ALT_VI, "altVi.equals=" + UPDATED_ALT_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altVi in
        defaultGalleryImageFiltering("altVi.in=" + DEFAULT_ALT_VI + "," + UPDATED_ALT_VI, "altVi.in=" + UPDATED_ALT_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altVi is not null
        defaultGalleryImageFiltering("altVi.specified=true", "altVi.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltViContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altVi contains
        defaultGalleryImageFiltering("altVi.contains=" + DEFAULT_ALT_VI, "altVi.contains=" + UPDATED_ALT_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altVi does not contain
        defaultGalleryImageFiltering("altVi.doesNotContain=" + UPDATED_ALT_VI, "altVi.doesNotContain=" + DEFAULT_ALT_VI);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altEn equals to
        defaultGalleryImageFiltering("altEn.equals=" + DEFAULT_ALT_EN, "altEn.equals=" + UPDATED_ALT_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altEn in
        defaultGalleryImageFiltering("altEn.in=" + DEFAULT_ALT_EN + "," + UPDATED_ALT_EN, "altEn.in=" + UPDATED_ALT_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altEn is not null
        defaultGalleryImageFiltering("altEn.specified=true", "altEn.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltEnContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altEn contains
        defaultGalleryImageFiltering("altEn.contains=" + DEFAULT_ALT_EN, "altEn.contains=" + UPDATED_ALT_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByAltEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where altEn does not contain
        defaultGalleryImageFiltering("altEn.doesNotContain=" + UPDATED_ALT_EN, "altEn.doesNotContain=" + DEFAULT_ALT_EN);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width equals to
        defaultGalleryImageFiltering("width.equals=" + DEFAULT_WIDTH, "width.equals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width in
        defaultGalleryImageFiltering("width.in=" + DEFAULT_WIDTH + "," + UPDATED_WIDTH, "width.in=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width is not null
        defaultGalleryImageFiltering("width.specified=true", "width.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width is greater than or equal to
        defaultGalleryImageFiltering("width.greaterThanOrEqual=" + DEFAULT_WIDTH, "width.greaterThanOrEqual=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width is less than or equal to
        defaultGalleryImageFiltering("width.lessThanOrEqual=" + DEFAULT_WIDTH, "width.lessThanOrEqual=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width is less than
        defaultGalleryImageFiltering("width.lessThan=" + UPDATED_WIDTH, "width.lessThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByWidthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where width is greater than
        defaultGalleryImageFiltering("width.greaterThan=" + SMALLER_WIDTH, "width.greaterThan=" + DEFAULT_WIDTH);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height equals to
        defaultGalleryImageFiltering("height.equals=" + DEFAULT_HEIGHT, "height.equals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height in
        defaultGalleryImageFiltering("height.in=" + DEFAULT_HEIGHT + "," + UPDATED_HEIGHT, "height.in=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height is not null
        defaultGalleryImageFiltering("height.specified=true", "height.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height is greater than or equal to
        defaultGalleryImageFiltering("height.greaterThanOrEqual=" + DEFAULT_HEIGHT, "height.greaterThanOrEqual=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height is less than or equal to
        defaultGalleryImageFiltering("height.lessThanOrEqual=" + DEFAULT_HEIGHT, "height.lessThanOrEqual=" + SMALLER_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height is less than
        defaultGalleryImageFiltering("height.lessThan=" + UPDATED_HEIGHT, "height.lessThan=" + DEFAULT_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByHeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where height is greater than
        defaultGalleryImageFiltering("height.greaterThan=" + SMALLER_HEIGHT, "height.greaterThan=" + DEFAULT_HEIGHT);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder equals to
        defaultGalleryImageFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder in
        defaultGalleryImageFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder is not null
        defaultGalleryImageFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder is greater than or equal to
        defaultGalleryImageFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder is less than or equal to
        defaultGalleryImageFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder is less than
        defaultGalleryImageFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where displayOrder is greater than
        defaultGalleryImageFiltering(
            "displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER,
            "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllGalleryImagesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where active equals to
        defaultGalleryImageFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where active in
        defaultGalleryImageFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllGalleryImagesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        // Get all the galleryImageList where active is not null
        defaultGalleryImageFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllGalleryImagesByBranchIsEqualToSomething() throws Exception {
        Branch branch;
        if (TestUtil.findAll(em, Branch.class).isEmpty()) {
            galleryImageRepository.saveAndFlush(galleryImage);
            branch = BranchResourceIT.createEntity();
        } else {
            branch = TestUtil.findAll(em, Branch.class).get(0);
        }
        em.persist(branch);
        em.flush();
        galleryImage.setBranch(branch);
        galleryImageRepository.saveAndFlush(galleryImage);
        Long branchId = branch.getId();
        // Get all the galleryImageList where branch equals to branchId
        defaultGalleryImageShouldBeFound("branchId.equals=" + branchId);

        // Get all the galleryImageList where branch equals to (branchId + 1)
        defaultGalleryImageShouldNotBeFound("branchId.equals=" + (branchId + 1));
    }

    private void defaultGalleryImageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultGalleryImageShouldBeFound(shouldBeFound);
        defaultGalleryImageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGalleryImageShouldBeFound(String filter) throws Exception {
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(galleryImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].captionVi").value(hasItem(DEFAULT_CAPTION_VI)))
            .andExpect(jsonPath("$.[*].captionEn").value(hasItem(DEFAULT_CAPTION_EN)))
            .andExpect(jsonPath("$.[*].altVi").value(hasItem(DEFAULT_ALT_VI)))
            .andExpect(jsonPath("$.[*].altEn").value(hasItem(DEFAULT_ALT_EN)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGalleryImageShouldNotBeFound(String filter) throws Exception {
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGalleryImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGalleryImage() throws Exception {
        // Get the galleryImage
        restGalleryImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGalleryImage() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the galleryImage
        GalleryImage updatedGalleryImage = galleryImageRepository.findById(galleryImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGalleryImage are not directly saved in db
        em.detach(updatedGalleryImage);
        updatedGalleryImage
            .imageUrl(UPDATED_IMAGE_URL)
            .captionVi(UPDATED_CAPTION_VI)
            .captionEn(UPDATED_CAPTION_EN)
            .altVi(UPDATED_ALT_VI)
            .altEn(UPDATED_ALT_EN)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(updatedGalleryImage);

        restGalleryImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, galleryImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(galleryImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGalleryImageToMatchAllProperties(updatedGalleryImage);
    }

    @Test
    @Transactional
    void putNonExistingGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, galleryImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(galleryImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(galleryImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGalleryImageWithPatch() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the galleryImage using partial update
        GalleryImage partialUpdatedGalleryImage = new GalleryImage();
        partialUpdatedGalleryImage.setId(galleryImage.getId());

        partialUpdatedGalleryImage.imageUrl(UPDATED_IMAGE_URL).captionVi(UPDATED_CAPTION_VI).height(UPDATED_HEIGHT).active(UPDATED_ACTIVE);

        restGalleryImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGalleryImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGalleryImage))
            )
            .andExpect(status().isOk());

        // Validate the GalleryImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGalleryImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGalleryImage, galleryImage),
            getPersistedGalleryImage(galleryImage)
        );
    }

    @Test
    @Transactional
    void fullUpdateGalleryImageWithPatch() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the galleryImage using partial update
        GalleryImage partialUpdatedGalleryImage = new GalleryImage();
        partialUpdatedGalleryImage.setId(galleryImage.getId());

        partialUpdatedGalleryImage
            .imageUrl(UPDATED_IMAGE_URL)
            .captionVi(UPDATED_CAPTION_VI)
            .captionEn(UPDATED_CAPTION_EN)
            .altVi(UPDATED_ALT_VI)
            .altEn(UPDATED_ALT_EN)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);

        restGalleryImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGalleryImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGalleryImage))
            )
            .andExpect(status().isOk());

        // Validate the GalleryImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGalleryImageUpdatableFieldsEquals(partialUpdatedGalleryImage, getPersistedGalleryImage(partialUpdatedGalleryImage));
    }

    @Test
    @Transactional
    void patchNonExistingGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, galleryImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(galleryImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(galleryImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGalleryImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        galleryImage.setId(longCount.incrementAndGet());

        // Create the GalleryImage
        GalleryImageDTO galleryImageDTO = galleryImageMapper.toDto(galleryImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGalleryImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(galleryImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GalleryImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGalleryImage() throws Exception {
        // Initialize the database
        insertedGalleryImage = galleryImageRepository.saveAndFlush(galleryImage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the galleryImage
        restGalleryImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, galleryImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return galleryImageRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected GalleryImage getPersistedGalleryImage(GalleryImage galleryImage) {
        return galleryImageRepository.findById(galleryImage.getId()).orElseThrow();
    }

    protected void assertPersistedGalleryImageToMatchAllProperties(GalleryImage expectedGalleryImage) {
        assertGalleryImageAllPropertiesEquals(expectedGalleryImage, getPersistedGalleryImage(expectedGalleryImage));
    }

    protected void assertPersistedGalleryImageToMatchUpdatableProperties(GalleryImage expectedGalleryImage) {
        assertGalleryImageAllUpdatablePropertiesEquals(expectedGalleryImage, getPersistedGalleryImage(expectedGalleryImage));
    }
}
