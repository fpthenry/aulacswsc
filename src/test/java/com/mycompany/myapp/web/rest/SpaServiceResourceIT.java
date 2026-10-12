package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SpaServiceAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.SpaServiceService;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.mapper.SpaServiceMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link SpaServiceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SpaServiceResourceIT {

    private static final String DEFAULT_NAME_VI = "AAAAAAAAAA";
    private static final String UPDATED_NAME_VI = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION_VI = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION_VI = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_VI = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_VI = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_BENEFITS_VI = "AAAAAAAAAA";
    private static final String UPDATED_BENEFITS_VI = "BBBBBBBBBB";

    private static final String DEFAULT_BENEFITS_EN = "AAAAAAAAAA";
    private static final String UPDATED_BENEFITS_EN = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES_VI = "AAAAAAAAAA";
    private static final String UPDATED_NOTES_VI = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES_EN = "AAAAAAAAAA";
    private static final String UPDATED_NOTES_EN = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_SALE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALE_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SALE_PRICE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_PRICE_MAX = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_MAX = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRICE_MAX = new BigDecimal(0 - 1);

    private static final Integer DEFAULT_DURATION_MINUTES = 15;
    private static final Integer UPDATED_DURATION_MINUTES = 16;
    private static final Integer SMALLER_DURATION_MINUTES = 15 - 1;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_FEATURED = false;
    private static final Boolean UPDATED_FEATURED = true;

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;
    private static final Integer SMALLER_DISPLAY_ORDER = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/spa-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SpaServiceRepository spaServiceRepository;

    @Mock
    private SpaServiceRepository spaServiceRepositoryMock;

    @Autowired
    private SpaServiceMapper spaServiceMapper;

    @Mock
    private SpaServiceService spaServiceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpaServiceMockMvc;

    private SpaService spaService;

    private SpaService insertedSpaService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaService createEntity(EntityManager em) {
        SpaService spaService = new SpaService()
            .nameVi(DEFAULT_NAME_VI)
            .nameEn(DEFAULT_NAME_EN)
            .slug(DEFAULT_SLUG)
            .shortDescriptionVi(DEFAULT_SHORT_DESCRIPTION_VI)
            .shortDescriptionEn(DEFAULT_SHORT_DESCRIPTION_EN)
            .descriptionVi(DEFAULT_DESCRIPTION_VI)
            .descriptionEn(DEFAULT_DESCRIPTION_EN)
            .benefitsVi(DEFAULT_BENEFITS_VI)
            .benefitsEn(DEFAULT_BENEFITS_EN)
            .notesVi(DEFAULT_NOTES_VI)
            .notesEn(DEFAULT_NOTES_EN)
            .price(DEFAULT_PRICE)
            .salePrice(DEFAULT_SALE_PRICE)
            .priceMax(DEFAULT_PRICE_MAX)
            .durationMinutes(DEFAULT_DURATION_MINUTES)
            .imageUrl(DEFAULT_IMAGE_URL)
            .featured(DEFAULT_FEATURED)
            .displayOrder(DEFAULT_DISPLAY_ORDER)
            .active(DEFAULT_ACTIVE);
        // Add required entity
        ServiceCategory serviceCategory;
        if (TestUtil.findAll(em, ServiceCategory.class).isEmpty()) {
            serviceCategory = ServiceCategoryResourceIT.createEntity();
            em.persist(serviceCategory);
            em.flush();
        } else {
            serviceCategory = TestUtil.findAll(em, ServiceCategory.class).get(0);
        }
        spaService.setCategory(serviceCategory);
        return spaService;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaService createUpdatedEntity(EntityManager em) {
        SpaService updatedSpaService = new SpaService()
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .shortDescriptionVi(UPDATED_SHORT_DESCRIPTION_VI)
            .shortDescriptionEn(UPDATED_SHORT_DESCRIPTION_EN)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .benefitsVi(UPDATED_BENEFITS_VI)
            .benefitsEn(UPDATED_BENEFITS_EN)
            .notesVi(UPDATED_NOTES_VI)
            .notesEn(UPDATED_NOTES_EN)
            .price(UPDATED_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .priceMax(UPDATED_PRICE_MAX)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .imageUrl(UPDATED_IMAGE_URL)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        // Add required entity
        ServiceCategory serviceCategory;
        if (TestUtil.findAll(em, ServiceCategory.class).isEmpty()) {
            serviceCategory = ServiceCategoryResourceIT.createUpdatedEntity();
            em.persist(serviceCategory);
            em.flush();
        } else {
            serviceCategory = TestUtil.findAll(em, ServiceCategory.class).get(0);
        }
        updatedSpaService.setCategory(serviceCategory);
        return updatedSpaService;
    }

    @BeforeEach
    void initTest() {
        spaService = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSpaService != null) {
            spaServiceRepository.delete(insertedSpaService);
            insertedSpaService = null;
        }
    }

    @Test
    @Transactional
    void createSpaService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);
        var returnedSpaServiceDTO = om.readValue(
            restSpaServiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SpaServiceDTO.class
        );

        // Validate the SpaService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSpaService = spaServiceMapper.toEntity(returnedSpaServiceDTO);
        assertSpaServiceUpdatableFieldsEquals(returnedSpaService, getPersistedSpaService(returnedSpaService));

        insertedSpaService = returnedSpaService;
    }

    @Test
    @Transactional
    void createSpaServiceWithExistingId() throws Exception {
        // Create the SpaService with an existing ID
        spaService.setId(1L);
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setNameVi(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setNameEn(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setSlug(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setPrice(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationMinutesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setDurationMinutes(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        spaService.setActive(null);

        // Create the SpaService, which fails.
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        restSpaServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpaServices() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spaService.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].shortDescriptionVi").value(hasItem(DEFAULT_SHORT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].shortDescriptionEn").value(hasItem(DEFAULT_SHORT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].descriptionVi").value(hasItem(DEFAULT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].benefitsVi").value(hasItem(DEFAULT_BENEFITS_VI)))
            .andExpect(jsonPath("$.[*].benefitsEn").value(hasItem(DEFAULT_BENEFITS_EN)))
            .andExpect(jsonPath("$.[*].notesVi").value(hasItem(DEFAULT_NOTES_VI)))
            .andExpect(jsonPath("$.[*].notesEn").value(hasItem(DEFAULT_NOTES_EN)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].priceMax").value(hasItem(sameNumber(DEFAULT_PRICE_MAX))))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].featured").value(hasItem(DEFAULT_FEATURED)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaServicesWithEagerRelationshipsIsEnabled() throws Exception {
        when(spaServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(spaServiceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaServicesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(spaServiceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaServiceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(spaServiceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSpaService() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get the spaService
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, spaService.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spaService.getId().intValue()))
            .andExpect(jsonPath("$.nameVi").value(DEFAULT_NAME_VI))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.shortDescriptionVi").value(DEFAULT_SHORT_DESCRIPTION_VI))
            .andExpect(jsonPath("$.shortDescriptionEn").value(DEFAULT_SHORT_DESCRIPTION_EN))
            .andExpect(jsonPath("$.descriptionVi").value(DEFAULT_DESCRIPTION_VI))
            .andExpect(jsonPath("$.descriptionEn").value(DEFAULT_DESCRIPTION_EN))
            .andExpect(jsonPath("$.benefitsVi").value(DEFAULT_BENEFITS_VI))
            .andExpect(jsonPath("$.benefitsEn").value(DEFAULT_BENEFITS_EN))
            .andExpect(jsonPath("$.notesVi").value(DEFAULT_NOTES_VI))
            .andExpect(jsonPath("$.notesEn").value(DEFAULT_NOTES_EN))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.salePrice").value(sameNumber(DEFAULT_SALE_PRICE)))
            .andExpect(jsonPath("$.priceMax").value(sameNumber(DEFAULT_PRICE_MAX)))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.featured").value(DEFAULT_FEATURED))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getSpaServicesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        Long id = spaService.getId();

        defaultSpaServiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSpaServiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSpaServiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameVi equals to
        defaultSpaServiceFiltering("nameVi.equals=" + DEFAULT_NAME_VI, "nameVi.equals=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameVi in
        defaultSpaServiceFiltering("nameVi.in=" + DEFAULT_NAME_VI + "," + UPDATED_NAME_VI, "nameVi.in=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameVi is not null
        defaultSpaServiceFiltering("nameVi.specified=true", "nameVi.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameViContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameVi contains
        defaultSpaServiceFiltering("nameVi.contains=" + DEFAULT_NAME_VI, "nameVi.contains=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameVi does not contain
        defaultSpaServiceFiltering("nameVi.doesNotContain=" + UPDATED_NAME_VI, "nameVi.doesNotContain=" + DEFAULT_NAME_VI);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameEn equals to
        defaultSpaServiceFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameEn in
        defaultSpaServiceFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameEn is not null
        defaultSpaServiceFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameEn contains
        defaultSpaServiceFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllSpaServicesByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where nameEn does not contain
        defaultSpaServiceFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where slug equals to
        defaultSpaServiceFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where slug in
        defaultSpaServiceFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where slug is not null
        defaultSpaServiceFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where slug contains
        defaultSpaServiceFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where slug does not contain
        defaultSpaServiceFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionVi equals to
        defaultSpaServiceFiltering(
            "shortDescriptionVi.equals=" + DEFAULT_SHORT_DESCRIPTION_VI,
            "shortDescriptionVi.equals=" + UPDATED_SHORT_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionVi in
        defaultSpaServiceFiltering(
            "shortDescriptionVi.in=" + DEFAULT_SHORT_DESCRIPTION_VI + "," + UPDATED_SHORT_DESCRIPTION_VI,
            "shortDescriptionVi.in=" + UPDATED_SHORT_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionVi is not null
        defaultSpaServiceFiltering("shortDescriptionVi.specified=true", "shortDescriptionVi.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionViContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionVi contains
        defaultSpaServiceFiltering(
            "shortDescriptionVi.contains=" + DEFAULT_SHORT_DESCRIPTION_VI,
            "shortDescriptionVi.contains=" + UPDATED_SHORT_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionVi does not contain
        defaultSpaServiceFiltering(
            "shortDescriptionVi.doesNotContain=" + UPDATED_SHORT_DESCRIPTION_VI,
            "shortDescriptionVi.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION_VI
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionEn equals to
        defaultSpaServiceFiltering(
            "shortDescriptionEn.equals=" + DEFAULT_SHORT_DESCRIPTION_EN,
            "shortDescriptionEn.equals=" + UPDATED_SHORT_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionEn in
        defaultSpaServiceFiltering(
            "shortDescriptionEn.in=" + DEFAULT_SHORT_DESCRIPTION_EN + "," + UPDATED_SHORT_DESCRIPTION_EN,
            "shortDescriptionEn.in=" + UPDATED_SHORT_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionEn is not null
        defaultSpaServiceFiltering("shortDescriptionEn.specified=true", "shortDescriptionEn.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionEnContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionEn contains
        defaultSpaServiceFiltering(
            "shortDescriptionEn.contains=" + DEFAULT_SHORT_DESCRIPTION_EN,
            "shortDescriptionEn.contains=" + UPDATED_SHORT_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByShortDescriptionEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where shortDescriptionEn does not contain
        defaultSpaServiceFiltering(
            "shortDescriptionEn.doesNotContain=" + UPDATED_SHORT_DESCRIPTION_EN,
            "shortDescriptionEn.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION_EN
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price equals to
        defaultSpaServiceFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price in
        defaultSpaServiceFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price is not null
        defaultSpaServiceFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price is greater than or equal to
        defaultSpaServiceFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price is less than or equal to
        defaultSpaServiceFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price is less than
        defaultSpaServiceFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where price is greater than
        defaultSpaServiceFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice equals to
        defaultSpaServiceFiltering("salePrice.equals=" + DEFAULT_SALE_PRICE, "salePrice.equals=" + UPDATED_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice in
        defaultSpaServiceFiltering("salePrice.in=" + DEFAULT_SALE_PRICE + "," + UPDATED_SALE_PRICE, "salePrice.in=" + UPDATED_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice is not null
        defaultSpaServiceFiltering("salePrice.specified=true", "salePrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice is greater than or equal to
        defaultSpaServiceFiltering(
            "salePrice.greaterThanOrEqual=" + DEFAULT_SALE_PRICE,
            "salePrice.greaterThanOrEqual=" + UPDATED_SALE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice is less than or equal to
        defaultSpaServiceFiltering("salePrice.lessThanOrEqual=" + DEFAULT_SALE_PRICE, "salePrice.lessThanOrEqual=" + SMALLER_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice is less than
        defaultSpaServiceFiltering("salePrice.lessThan=" + UPDATED_SALE_PRICE, "salePrice.lessThan=" + DEFAULT_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesBySalePriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where salePrice is greater than
        defaultSpaServiceFiltering("salePrice.greaterThan=" + SMALLER_SALE_PRICE, "salePrice.greaterThan=" + DEFAULT_SALE_PRICE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax equals to
        defaultSpaServiceFiltering("priceMax.equals=" + DEFAULT_PRICE_MAX, "priceMax.equals=" + UPDATED_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax in
        defaultSpaServiceFiltering("priceMax.in=" + DEFAULT_PRICE_MAX + "," + UPDATED_PRICE_MAX, "priceMax.in=" + UPDATED_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax is not null
        defaultSpaServiceFiltering("priceMax.specified=true", "priceMax.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax is greater than or equal to
        defaultSpaServiceFiltering("priceMax.greaterThanOrEqual=" + DEFAULT_PRICE_MAX, "priceMax.greaterThanOrEqual=" + UPDATED_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax is less than or equal to
        defaultSpaServiceFiltering("priceMax.lessThanOrEqual=" + DEFAULT_PRICE_MAX, "priceMax.lessThanOrEqual=" + SMALLER_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax is less than
        defaultSpaServiceFiltering("priceMax.lessThan=" + UPDATED_PRICE_MAX, "priceMax.lessThan=" + DEFAULT_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByPriceMaxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where priceMax is greater than
        defaultSpaServiceFiltering("priceMax.greaterThan=" + SMALLER_PRICE_MAX, "priceMax.greaterThan=" + DEFAULT_PRICE_MAX);
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes equals to
        defaultSpaServiceFiltering(
            "durationMinutes.equals=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.equals=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes in
        defaultSpaServiceFiltering(
            "durationMinutes.in=" + DEFAULT_DURATION_MINUTES + "," + UPDATED_DURATION_MINUTES,
            "durationMinutes.in=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes is not null
        defaultSpaServiceFiltering("durationMinutes.specified=true", "durationMinutes.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes is greater than or equal to
        defaultSpaServiceFiltering(
            "durationMinutes.greaterThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.greaterThanOrEqual=" + UPDATED_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes is less than or equal to
        defaultSpaServiceFiltering(
            "durationMinutes.lessThanOrEqual=" + DEFAULT_DURATION_MINUTES,
            "durationMinutes.lessThanOrEqual=" + SMALLER_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes is less than
        defaultSpaServiceFiltering(
            "durationMinutes.lessThan=" + UPDATED_DURATION_MINUTES,
            "durationMinutes.lessThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDurationMinutesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where durationMinutes is greater than
        defaultSpaServiceFiltering(
            "durationMinutes.greaterThan=" + SMALLER_DURATION_MINUTES,
            "durationMinutes.greaterThan=" + DEFAULT_DURATION_MINUTES
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where imageUrl equals to
        defaultSpaServiceFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllSpaServicesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where imageUrl in
        defaultSpaServiceFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllSpaServicesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where imageUrl is not null
        defaultSpaServiceFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where imageUrl contains
        defaultSpaServiceFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllSpaServicesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where imageUrl does not contain
        defaultSpaServiceFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllSpaServicesByFeaturedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where featured equals to
        defaultSpaServiceFiltering("featured.equals=" + DEFAULT_FEATURED, "featured.equals=" + UPDATED_FEATURED);
    }

    @Test
    @Transactional
    void getAllSpaServicesByFeaturedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where featured in
        defaultSpaServiceFiltering("featured.in=" + DEFAULT_FEATURED + "," + UPDATED_FEATURED, "featured.in=" + UPDATED_FEATURED);
    }

    @Test
    @Transactional
    void getAllSpaServicesByFeaturedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where featured is not null
        defaultSpaServiceFiltering("featured.specified=true", "featured.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder equals to
        defaultSpaServiceFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder in
        defaultSpaServiceFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder is not null
        defaultSpaServiceFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder is greater than or equal to
        defaultSpaServiceFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder is less than or equal to
        defaultSpaServiceFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder is less than
        defaultSpaServiceFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllSpaServicesByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where displayOrder is greater than
        defaultSpaServiceFiltering(
            "displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER,
            "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllSpaServicesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where active equals to
        defaultSpaServiceFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where active in
        defaultSpaServiceFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllSpaServicesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        // Get all the spaServiceList where active is not null
        defaultSpaServiceFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllSpaServicesByCategoryIsEqualToSomething() throws Exception {
        ServiceCategory category;
        if (TestUtil.findAll(em, ServiceCategory.class).isEmpty()) {
            spaServiceRepository.saveAndFlush(spaService);
            category = ServiceCategoryResourceIT.createEntity();
        } else {
            category = TestUtil.findAll(em, ServiceCategory.class).get(0);
        }
        em.persist(category);
        em.flush();
        spaService.setCategory(category);
        spaServiceRepository.saveAndFlush(spaService);
        Long categoryId = category.getId();
        // Get all the spaServiceList where category equals to categoryId
        defaultSpaServiceShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the spaServiceList where category equals to (categoryId + 1)
        defaultSpaServiceShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllSpaServicesByStaffsIsEqualToSomething() throws Exception {
        Staff staffs;
        if (TestUtil.findAll(em, Staff.class).isEmpty()) {
            spaServiceRepository.saveAndFlush(spaService);
            staffs = StaffResourceIT.createEntity();
        } else {
            staffs = TestUtil.findAll(em, Staff.class).get(0);
        }
        em.persist(staffs);
        em.flush();
        spaService.addStaffs(staffs);
        spaServiceRepository.saveAndFlush(spaService);
        Long staffsId = staffs.getId();
        // Get all the spaServiceList where staffs equals to staffsId
        defaultSpaServiceShouldBeFound("staffsId.equals=" + staffsId);

        // Get all the spaServiceList where staffs equals to (staffsId + 1)
        defaultSpaServiceShouldNotBeFound("staffsId.equals=" + (staffsId + 1));
    }

    private void defaultSpaServiceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSpaServiceShouldBeFound(shouldBeFound);
        defaultSpaServiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpaServiceShouldBeFound(String filter) throws Exception {
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spaService.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].shortDescriptionVi").value(hasItem(DEFAULT_SHORT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].shortDescriptionEn").value(hasItem(DEFAULT_SHORT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].descriptionVi").value(hasItem(DEFAULT_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].benefitsVi").value(hasItem(DEFAULT_BENEFITS_VI)))
            .andExpect(jsonPath("$.[*].benefitsEn").value(hasItem(DEFAULT_BENEFITS_EN)))
            .andExpect(jsonPath("$.[*].notesVi").value(hasItem(DEFAULT_NOTES_VI)))
            .andExpect(jsonPath("$.[*].notesEn").value(hasItem(DEFAULT_NOTES_EN)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].salePrice").value(hasItem(sameNumber(DEFAULT_SALE_PRICE))))
            .andExpect(jsonPath("$.[*].priceMax").value(hasItem(sameNumber(DEFAULT_PRICE_MAX))))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].featured").value(hasItem(DEFAULT_FEATURED)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpaServiceShouldNotBeFound(String filter) throws Exception {
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpaServiceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpaService() throws Exception {
        // Get the spaService
        restSpaServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpaService() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaService
        SpaService updatedSpaService = spaServiceRepository.findById(spaService.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSpaService are not directly saved in db
        em.detach(updatedSpaService);
        updatedSpaService
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .shortDescriptionVi(UPDATED_SHORT_DESCRIPTION_VI)
            .shortDescriptionEn(UPDATED_SHORT_DESCRIPTION_EN)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .benefitsVi(UPDATED_BENEFITS_VI)
            .benefitsEn(UPDATED_BENEFITS_EN)
            .notesVi(UPDATED_NOTES_VI)
            .notesEn(UPDATED_NOTES_EN)
            .price(UPDATED_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .priceMax(UPDATED_PRICE_MAX)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .imageUrl(UPDATED_IMAGE_URL)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(updatedSpaService);

        restSpaServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaServiceDTO))
            )
            .andExpect(status().isOk());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSpaServiceToMatchAllProperties(updatedSpaService);
    }

    @Test
    @Transactional
    void putNonExistingSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaServiceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(spaServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpaServiceWithPatch() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaService using partial update
        SpaService partialUpdatedSpaService = new SpaService();
        partialUpdatedSpaService.setId(spaService.getId());

        partialUpdatedSpaService
            .slug(UPDATED_SLUG)
            .shortDescriptionVi(UPDATED_SHORT_DESCRIPTION_VI)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .priceMax(UPDATED_PRICE_MAX)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER);

        restSpaServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpaService))
            )
            .andExpect(status().isOk());

        // Validate the SpaService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpaServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSpaService, spaService),
            getPersistedSpaService(spaService)
        );
    }

    @Test
    @Transactional
    void fullUpdateSpaServiceWithPatch() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the spaService using partial update
        SpaService partialUpdatedSpaService = new SpaService();
        partialUpdatedSpaService.setId(spaService.getId());

        partialUpdatedSpaService
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .shortDescriptionVi(UPDATED_SHORT_DESCRIPTION_VI)
            .shortDescriptionEn(UPDATED_SHORT_DESCRIPTION_EN)
            .descriptionVi(UPDATED_DESCRIPTION_VI)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .benefitsVi(UPDATED_BENEFITS_VI)
            .benefitsEn(UPDATED_BENEFITS_EN)
            .notesVi(UPDATED_NOTES_VI)
            .notesEn(UPDATED_NOTES_EN)
            .price(UPDATED_PRICE)
            .salePrice(UPDATED_SALE_PRICE)
            .priceMax(UPDATED_PRICE_MAX)
            .durationMinutes(UPDATED_DURATION_MINUTES)
            .imageUrl(UPDATED_IMAGE_URL)
            .featured(UPDATED_FEATURED)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);

        restSpaServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpaService))
            )
            .andExpect(status().isOk());

        // Validate the SpaService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpaServiceUpdatableFieldsEquals(partialUpdatedSpaService, getPersistedSpaService(partialUpdatedSpaService));
    }

    @Test
    @Transactional
    void patchNonExistingSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spaServiceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(spaServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(spaServiceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpaService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        spaService.setId(longCount.incrementAndGet());

        // Create the SpaService
        SpaServiceDTO spaServiceDTO = spaServiceMapper.toDto(spaService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaServiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(spaServiceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpaService() throws Exception {
        // Initialize the database
        insertedSpaService = spaServiceRepository.saveAndFlush(spaService);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the spaService
        restSpaServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, spaService.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return spaServiceRepository.count();
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

    protected SpaService getPersistedSpaService(SpaService spaService) {
        return spaServiceRepository.findById(spaService.getId()).orElseThrow();
    }

    protected void assertPersistedSpaServiceToMatchAllProperties(SpaService expectedSpaService) {
        assertSpaServiceAllPropertiesEquals(expectedSpaService, getPersistedSpaService(expectedSpaService));
    }

    protected void assertPersistedSpaServiceToMatchUpdatableProperties(SpaService expectedSpaService) {
        assertSpaServiceAllUpdatablePropertiesEquals(expectedSpaService, getPersistedSpaService(expectedSpaService));
    }
}
