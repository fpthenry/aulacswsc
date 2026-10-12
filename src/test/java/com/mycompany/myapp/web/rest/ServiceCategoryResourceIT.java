package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ServiceCategoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.repository.ServiceCategoryRepository;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.mapper.ServiceCategoryMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ServiceCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceCategoryResourceIT {

    private static final String DEFAULT_NAME_VI = "AAAAAAAAAA";
    private static final String UPDATED_NAME_VI = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_VI = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_VI = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;
    private static final Integer SMALLER_DISPLAY_ORDER = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/service-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceCategoryMockMvc;

    private ServiceCategory serviceCategory;

    private ServiceCategory insertedServiceCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceCategory createEntity() {
        return new ServiceCategory()
            .nameVi(DEFAULT_NAME_VI)
            .nameEn(DEFAULT_NAME_EN)
            .slug(DEFAULT_SLUG)
            .descriptionVi(DEFAULT_DESCRIPTION_VI)
            .descriptionEn(DEFAULT_DESCRIPTION_EN)
            .imageUrl(DEFAULT_IMAGE_URL)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceCategory createUpdatedEntity() {
        return new ServiceCategory()
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        serviceCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedServiceCategory != null) {
            serviceCategoryRepository.delete(insertedServiceCategory);
            insertedServiceCategory = null;
        }
    }

    @Test
    @Transactional
    void createServiceCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);
        var returnedServiceCategoryDTO = om.readValue(
            restServiceCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ServiceCategoryDTO.class
        );

        // Validate the ServiceCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedServiceCategory = serviceCategoryMapper.toEntity(returnedServiceCategoryDTO);
        assertServiceCategoryUpdatableFieldsEquals(returnedServiceCategory, getPersistedServiceCategory(returnedServiceCategory));

        insertedServiceCategory = returnedServiceCategory;
    }

    @Test
    @Transactional
    void createServiceCategoryWithExistingId() throws Exception {
        // Create the ServiceCategory with an existing ID
        serviceCategory.setId(1L);
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceCategory.setNameVi(null);

        // Create the ServiceCategory, which fails.
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        restServiceCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceCategory.setNameEn(null);

        // Create the ServiceCategory, which fails.
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        restServiceCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceCategory.setSlug(null);

        // Create the ServiceCategory, which fails.
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        restServiceCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        serviceCategory.setActive(null);

        // Create the ServiceCategory, which fails.
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        restServiceCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServiceCategories() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].descriptionVi").value(hasItem(DEFAULT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getServiceCategory() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get the serviceCategory
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceCategory.getId().intValue()))
            .andExpect(jsonPath("$.nameVi").value(DEFAULT_NAME_VI))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.descriptionVi").value(DEFAULT_DESCRIPTION_VI))
            .andExpect(jsonPath("$.descriptionEn").value(DEFAULT_DESCRIPTION_EN))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getServiceCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        Long id = serviceCategory.getId();

        defaultServiceCategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultServiceCategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultServiceCategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameVi equals to
        defaultServiceCategoryFiltering("nameVi.equals=" + DEFAULT_NAME_VI, "nameVi.equals=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameVi in
        defaultServiceCategoryFiltering("nameVi.in=" + DEFAULT_NAME_VI + "," + UPDATED_NAME_VI, "nameVi.in=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameVi is not null
        defaultServiceCategoryFiltering("nameVi.specified=true", "nameVi.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameViContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameVi contains
        defaultServiceCategoryFiltering("nameVi.contains=" + DEFAULT_NAME_VI, "nameVi.contains=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameVi does not contain
        defaultServiceCategoryFiltering("nameVi.doesNotContain=" + UPDATED_NAME_VI, "nameVi.doesNotContain=" + DEFAULT_NAME_VI);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameEn equals to
        defaultServiceCategoryFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameEn in
        defaultServiceCategoryFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameEn is not null
        defaultServiceCategoryFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameEn contains
        defaultServiceCategoryFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where nameEn does not contain
        defaultServiceCategoryFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where slug equals to
        defaultServiceCategoryFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where slug in
        defaultServiceCategoryFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where slug is not null
        defaultServiceCategoryFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where slug contains
        defaultServiceCategoryFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where slug does not contain
        defaultServiceCategoryFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionVi equals to
        defaultServiceCategoryFiltering("descriptionVi.equals=" + DEFAULT_DESCRIPTION_VI, "descriptionVi.equals=" + UPDATED_DESCRIPTION_VI);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionVi in
        defaultServiceCategoryFiltering(
            "descriptionVi.in=" + DEFAULT_DESCRIPTION_VI + "," + UPDATED_DESCRIPTION_VI,
            "descriptionVi.in=" + UPDATED_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionVi is not null
        defaultServiceCategoryFiltering("descriptionVi.specified=true", "descriptionVi.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionViContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionVi contains
        defaultServiceCategoryFiltering(
            "descriptionVi.contains=" + DEFAULT_DESCRIPTION_VI,
            "descriptionVi.contains=" + UPDATED_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionVi does not contain
        defaultServiceCategoryFiltering(
            "descriptionVi.doesNotContain=" + UPDATED_DESCRIPTION_VI,
            "descriptionVi.doesNotContain=" + DEFAULT_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionEn equals to
        defaultServiceCategoryFiltering("descriptionEn.equals=" + DEFAULT_DESCRIPTION_EN, "descriptionEn.equals=" + UPDATED_DESCRIPTION_EN);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionEn in
        defaultServiceCategoryFiltering(
            "descriptionEn.in=" + DEFAULT_DESCRIPTION_EN + "," + UPDATED_DESCRIPTION_EN,
            "descriptionEn.in=" + UPDATED_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionEn is not null
        defaultServiceCategoryFiltering("descriptionEn.specified=true", "descriptionEn.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionEnContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionEn contains
        defaultServiceCategoryFiltering(
            "descriptionEn.contains=" + DEFAULT_DESCRIPTION_EN,
            "descriptionEn.contains=" + UPDATED_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDescriptionEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where descriptionEn does not contain
        defaultServiceCategoryFiltering(
            "descriptionEn.doesNotContain=" + UPDATED_DESCRIPTION_EN,
            "descriptionEn.doesNotContain=" + DEFAULT_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where imageUrl equals to
        defaultServiceCategoryFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where imageUrl in
        defaultServiceCategoryFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where imageUrl is not null
        defaultServiceCategoryFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where imageUrl contains
        defaultServiceCategoryFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where imageUrl does not contain
        defaultServiceCategoryFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder equals to
        defaultServiceCategoryFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder in
        defaultServiceCategoryFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder is not null
        defaultServiceCategoryFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder is greater than or equal to
        defaultServiceCategoryFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder is less than or equal to
        defaultServiceCategoryFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder is less than
        defaultServiceCategoryFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where displayOrder is greater than
        defaultServiceCategoryFiltering(
            "displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER,
            "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where active equals to
        defaultServiceCategoryFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where active in
        defaultServiceCategoryFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllServiceCategoriesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        // Get all the serviceCategoryList where active is not null
        defaultServiceCategoryFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultServiceCategoryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultServiceCategoryShouldBeFound(shouldBeFound);
        defaultServiceCategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServiceCategoryShouldBeFound(String filter) throws Exception {
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].descriptionVi").value(hasItem(DEFAULT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServiceCategoryShouldNotBeFound(String filter) throws Exception {
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServiceCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingServiceCategory() throws Exception {
        // Get the serviceCategory
        restServiceCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingServiceCategory() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceCategory
        ServiceCategory updatedServiceCategory = serviceCategoryRepository.findById(serviceCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedServiceCategory are not directly saved in db
        em.detach(updatedServiceCategory);
        updatedServiceCategory
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(updatedServiceCategory);

        restServiceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceCategoryToMatchAllProperties(updatedServiceCategory);
    }

    @Test
    @Transactional
    void putNonExistingServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(serviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServiceCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceCategory using partial update
        ServiceCategory partialUpdatedServiceCategory = new ServiceCategory();
        partialUpdatedServiceCategory.setId(serviceCategory.getId());

        partialUpdatedServiceCategory.descriptionVi(UPDATED_DESCRIPTION_VI);

        restServiceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceCategory))
            )
            .andExpect(status().isOk());

        // Validate the ServiceCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedServiceCategory, serviceCategory),
            getPersistedServiceCategory(serviceCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdateServiceCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the serviceCategory using partial update
        ServiceCategory partialUpdatedServiceCategory = new ServiceCategory();
        partialUpdatedServiceCategory.setId(serviceCategory.getId());

        partialUpdatedServiceCategory
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);

        restServiceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedServiceCategory))
            )
            .andExpect(status().isOk());

        // Validate the ServiceCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceCategoryUpdatableFieldsEquals(
            partialUpdatedServiceCategory,
            getPersistedServiceCategory(partialUpdatedServiceCategory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(serviceCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        serviceCategory.setId(longCount.incrementAndGet());

        // Create the ServiceCategory
        ServiceCategoryDTO serviceCategoryDTO = serviceCategoryMapper.toDto(serviceCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(serviceCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteServiceCategory() throws Exception {
        // Initialize the database
        insertedServiceCategory = serviceCategoryRepository.saveAndFlush(serviceCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the serviceCategory
        restServiceCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceCategoryRepository.count();
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

    protected ServiceCategory getPersistedServiceCategory(ServiceCategory serviceCategory) {
        return serviceCategoryRepository.findById(serviceCategory.getId()).orElseThrow();
    }

    protected void assertPersistedServiceCategoryToMatchAllProperties(ServiceCategory expectedServiceCategory) {
        assertServiceCategoryAllPropertiesEquals(expectedServiceCategory, getPersistedServiceCategory(expectedServiceCategory));
    }

    protected void assertPersistedServiceCategoryToMatchUpdatableProperties(ServiceCategory expectedServiceCategory) {
        assertServiceCategoryAllUpdatablePropertiesEquals(expectedServiceCategory, getPersistedServiceCategory(expectedServiceCategory));
    }
}
