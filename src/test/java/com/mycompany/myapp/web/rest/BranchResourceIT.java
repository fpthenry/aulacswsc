package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BranchAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.repository.BranchRepository;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.mapper.BranchMapper;
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
 * Integration tests for the {@link BranchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BranchResourceIT {

    private static final String DEFAULT_NAME_VI = "AAAAAAAAAA";
    private static final String UPDATED_NAME_VI = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_NAME_EN = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_VI = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_VI = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_EN = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_EN = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WHATSAPP = "AAAAAAAAAA";
    private static final String UPDATED_WHATSAPP = "BBBBBBBBBB";

    private static final String DEFAULT_ZALO = "AAAAAAAAAA";
    private static final String UPDATED_ZALO = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_OPEN_TIME = "07:11";
    private static final String UPDATED_OPEN_TIME = "01:05";

    private static final String DEFAULT_CLOSE_TIME = "10:47";
    private static final String UPDATED_CLOSE_TIME = "22:07";

    private static final String DEFAULT_LAST_BOOKING_TIME = "23:38";
    private static final String UPDATED_LAST_BOOKING_TIME = "20:02";

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;
    private static final Integer SMALLER_CAPACITY = 1 - 1;

    private static final String DEFAULT_MAP_EMBED_URL = "AAAAAAAAAA";
    private static final String UPDATED_MAP_EMBED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_MAP_DIRECTION_URL = "AAAAAAAAAA";
    private static final String UPDATED_MAP_DIRECTION_URL = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISPLAY_ORDER = 1;
    private static final Integer UPDATED_DISPLAY_ORDER = 2;
    private static final Integer SMALLER_DISPLAY_ORDER = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/branches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBranchMockMvc;

    private Branch branch;

    private Branch insertedBranch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branch createEntity() {
        return new Branch()
            .nameVi(DEFAULT_NAME_VI)
            .nameEn(DEFAULT_NAME_EN)
            .slug(DEFAULT_SLUG)
            .addressVi(DEFAULT_ADDRESS_VI)
            .addressEn(DEFAULT_ADDRESS_EN)
            .phone(DEFAULT_PHONE)
            .whatsapp(DEFAULT_WHATSAPP)
            .zalo(DEFAULT_ZALO)
            .email(DEFAULT_EMAIL)
            .openTime(DEFAULT_OPEN_TIME)
            .closeTime(DEFAULT_CLOSE_TIME)
            .lastBookingTime(DEFAULT_LAST_BOOKING_TIME)
            .capacity(DEFAULT_CAPACITY)
            .mapEmbedUrl(DEFAULT_MAP_EMBED_URL)
            .mapDirectionUrl(DEFAULT_MAP_DIRECTION_URL)
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
    public static Branch createUpdatedEntity() {
        return new Branch()
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .addressVi(UPDATED_ADDRESS_VI)
            .addressEn(UPDATED_ADDRESS_EN)
            .phone(UPDATED_PHONE)
            .whatsapp(UPDATED_WHATSAPP)
            .zalo(UPDATED_ZALO)
            .email(UPDATED_EMAIL)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .lastBookingTime(UPDATED_LAST_BOOKING_TIME)
            .capacity(UPDATED_CAPACITY)
            .mapEmbedUrl(UPDATED_MAP_EMBED_URL)
            .mapDirectionUrl(UPDATED_MAP_DIRECTION_URL)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        branch = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBranch != null) {
            branchRepository.delete(insertedBranch);
            insertedBranch = null;
        }
    }

    @Test
    @Transactional
    void createBranch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);
        var returnedBranchDTO = om.readValue(
            restBranchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BranchDTO.class
        );

        // Validate the Branch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBranch = branchMapper.toEntity(returnedBranchDTO);
        assertBranchUpdatableFieldsEquals(returnedBranch, getPersistedBranch(returnedBranch));

        insertedBranch = returnedBranch;
    }

    @Test
    @Transactional
    void createBranchWithExistingId() throws Exception {
        // Create the Branch with an existing ID
        branch.setId(1L);
        BranchDTO branchDTO = branchMapper.toDto(branch);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setNameVi(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setNameEn(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setSlug(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setAddressVi(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setPhone(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOpenTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setOpenTime(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCloseTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setCloseTime(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastBookingTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setLastBookingTime(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setCapacity(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        branch.setActive(null);

        // Create the Branch, which fails.
        BranchDTO branchDTO = branchMapper.toDto(branch);

        restBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBranches() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList
        restBranchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branch.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].addressVi").value(hasItem(DEFAULT_ADDRESS_VI)))
            .andExpect(jsonPath("$.[*].addressEn").value(hasItem(DEFAULT_ADDRESS_EN)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].whatsapp").value(hasItem(DEFAULT_WHATSAPP)))
            .andExpect(jsonPath("$.[*].zalo").value(hasItem(DEFAULT_ZALO)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].openTime").value(hasItem(DEFAULT_OPEN_TIME)))
            .andExpect(jsonPath("$.[*].closeTime").value(hasItem(DEFAULT_CLOSE_TIME)))
            .andExpect(jsonPath("$.[*].lastBookingTime").value(hasItem(DEFAULT_LAST_BOOKING_TIME)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].mapEmbedUrl").value(hasItem(DEFAULT_MAP_EMBED_URL)))
            .andExpect(jsonPath("$.[*].mapDirectionUrl").value(hasItem(DEFAULT_MAP_DIRECTION_URL)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getBranch() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get the branch
        restBranchMockMvc
            .perform(get(ENTITY_API_URL_ID, branch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(branch.getId().intValue()))
            .andExpect(jsonPath("$.nameVi").value(DEFAULT_NAME_VI))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.addressVi").value(DEFAULT_ADDRESS_VI))
            .andExpect(jsonPath("$.addressEn").value(DEFAULT_ADDRESS_EN))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.whatsapp").value(DEFAULT_WHATSAPP))
            .andExpect(jsonPath("$.zalo").value(DEFAULT_ZALO))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.openTime").value(DEFAULT_OPEN_TIME))
            .andExpect(jsonPath("$.closeTime").value(DEFAULT_CLOSE_TIME))
            .andExpect(jsonPath("$.lastBookingTime").value(DEFAULT_LAST_BOOKING_TIME))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.mapEmbedUrl").value(DEFAULT_MAP_EMBED_URL))
            .andExpect(jsonPath("$.mapDirectionUrl").value(DEFAULT_MAP_DIRECTION_URL))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.displayOrder").value(DEFAULT_DISPLAY_ORDER))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getBranchesByIdFiltering() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        Long id = branch.getId();

        defaultBranchFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBranchFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBranchFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBranchesByNameViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameVi equals to
        defaultBranchFiltering("nameVi.equals=" + DEFAULT_NAME_VI, "nameVi.equals=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByNameViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameVi in
        defaultBranchFiltering("nameVi.in=" + DEFAULT_NAME_VI + "," + UPDATED_NAME_VI, "nameVi.in=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByNameViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameVi is not null
        defaultBranchFiltering("nameVi.specified=true", "nameVi.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByNameViContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameVi contains
        defaultBranchFiltering("nameVi.contains=" + DEFAULT_NAME_VI, "nameVi.contains=" + UPDATED_NAME_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByNameViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameVi does not contain
        defaultBranchFiltering("nameVi.doesNotContain=" + UPDATED_NAME_VI, "nameVi.doesNotContain=" + DEFAULT_NAME_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByNameEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameEn equals to
        defaultBranchFiltering("nameEn.equals=" + DEFAULT_NAME_EN, "nameEn.equals=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByNameEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameEn in
        defaultBranchFiltering("nameEn.in=" + DEFAULT_NAME_EN + "," + UPDATED_NAME_EN, "nameEn.in=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByNameEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameEn is not null
        defaultBranchFiltering("nameEn.specified=true", "nameEn.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByNameEnContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameEn contains
        defaultBranchFiltering("nameEn.contains=" + DEFAULT_NAME_EN, "nameEn.contains=" + UPDATED_NAME_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByNameEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where nameEn does not contain
        defaultBranchFiltering("nameEn.doesNotContain=" + UPDATED_NAME_EN, "nameEn.doesNotContain=" + DEFAULT_NAME_EN);
    }

    @Test
    @Transactional
    void getAllBranchesBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where slug equals to
        defaultBranchFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBranchesBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where slug in
        defaultBranchFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBranchesBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where slug is not null
        defaultBranchFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where slug contains
        defaultBranchFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBranchesBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where slug does not contain
        defaultBranchFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressVi equals to
        defaultBranchFiltering("addressVi.equals=" + DEFAULT_ADDRESS_VI, "addressVi.equals=" + UPDATED_ADDRESS_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressVi in
        defaultBranchFiltering("addressVi.in=" + DEFAULT_ADDRESS_VI + "," + UPDATED_ADDRESS_VI, "addressVi.in=" + UPDATED_ADDRESS_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressVi is not null
        defaultBranchFiltering("addressVi.specified=true", "addressVi.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByAddressViContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressVi contains
        defaultBranchFiltering("addressVi.contains=" + DEFAULT_ADDRESS_VI, "addressVi.contains=" + UPDATED_ADDRESS_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressVi does not contain
        defaultBranchFiltering("addressVi.doesNotContain=" + UPDATED_ADDRESS_VI, "addressVi.doesNotContain=" + DEFAULT_ADDRESS_VI);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressEn equals to
        defaultBranchFiltering("addressEn.equals=" + DEFAULT_ADDRESS_EN, "addressEn.equals=" + UPDATED_ADDRESS_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressEn in
        defaultBranchFiltering("addressEn.in=" + DEFAULT_ADDRESS_EN + "," + UPDATED_ADDRESS_EN, "addressEn.in=" + UPDATED_ADDRESS_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressEn is not null
        defaultBranchFiltering("addressEn.specified=true", "addressEn.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByAddressEnContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressEn contains
        defaultBranchFiltering("addressEn.contains=" + DEFAULT_ADDRESS_EN, "addressEn.contains=" + UPDATED_ADDRESS_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByAddressEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where addressEn does not contain
        defaultBranchFiltering("addressEn.doesNotContain=" + UPDATED_ADDRESS_EN, "addressEn.doesNotContain=" + DEFAULT_ADDRESS_EN);
    }

    @Test
    @Transactional
    void getAllBranchesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where phone equals to
        defaultBranchFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBranchesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where phone in
        defaultBranchFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBranchesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where phone is not null
        defaultBranchFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where phone contains
        defaultBranchFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBranchesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where phone does not contain
        defaultBranchFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllBranchesByWhatsappIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where whatsapp equals to
        defaultBranchFiltering("whatsapp.equals=" + DEFAULT_WHATSAPP, "whatsapp.equals=" + UPDATED_WHATSAPP);
    }

    @Test
    @Transactional
    void getAllBranchesByWhatsappIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where whatsapp in
        defaultBranchFiltering("whatsapp.in=" + DEFAULT_WHATSAPP + "," + UPDATED_WHATSAPP, "whatsapp.in=" + UPDATED_WHATSAPP);
    }

    @Test
    @Transactional
    void getAllBranchesByWhatsappIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where whatsapp is not null
        defaultBranchFiltering("whatsapp.specified=true", "whatsapp.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByWhatsappContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where whatsapp contains
        defaultBranchFiltering("whatsapp.contains=" + DEFAULT_WHATSAPP, "whatsapp.contains=" + UPDATED_WHATSAPP);
    }

    @Test
    @Transactional
    void getAllBranchesByWhatsappNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where whatsapp does not contain
        defaultBranchFiltering("whatsapp.doesNotContain=" + UPDATED_WHATSAPP, "whatsapp.doesNotContain=" + DEFAULT_WHATSAPP);
    }

    @Test
    @Transactional
    void getAllBranchesByZaloIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where zalo equals to
        defaultBranchFiltering("zalo.equals=" + DEFAULT_ZALO, "zalo.equals=" + UPDATED_ZALO);
    }

    @Test
    @Transactional
    void getAllBranchesByZaloIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where zalo in
        defaultBranchFiltering("zalo.in=" + DEFAULT_ZALO + "," + UPDATED_ZALO, "zalo.in=" + UPDATED_ZALO);
    }

    @Test
    @Transactional
    void getAllBranchesByZaloIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where zalo is not null
        defaultBranchFiltering("zalo.specified=true", "zalo.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByZaloContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where zalo contains
        defaultBranchFiltering("zalo.contains=" + DEFAULT_ZALO, "zalo.contains=" + UPDATED_ZALO);
    }

    @Test
    @Transactional
    void getAllBranchesByZaloNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where zalo does not contain
        defaultBranchFiltering("zalo.doesNotContain=" + UPDATED_ZALO, "zalo.doesNotContain=" + DEFAULT_ZALO);
    }

    @Test
    @Transactional
    void getAllBranchesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where email equals to
        defaultBranchFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllBranchesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where email in
        defaultBranchFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllBranchesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where email is not null
        defaultBranchFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where email contains
        defaultBranchFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllBranchesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where email does not contain
        defaultBranchFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllBranchesByOpenTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where openTime equals to
        defaultBranchFiltering("openTime.equals=" + DEFAULT_OPEN_TIME, "openTime.equals=" + UPDATED_OPEN_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByOpenTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where openTime in
        defaultBranchFiltering("openTime.in=" + DEFAULT_OPEN_TIME + "," + UPDATED_OPEN_TIME, "openTime.in=" + UPDATED_OPEN_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByOpenTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where openTime is not null
        defaultBranchFiltering("openTime.specified=true", "openTime.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByOpenTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where openTime contains
        defaultBranchFiltering("openTime.contains=" + DEFAULT_OPEN_TIME, "openTime.contains=" + UPDATED_OPEN_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByOpenTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where openTime does not contain
        defaultBranchFiltering("openTime.doesNotContain=" + UPDATED_OPEN_TIME, "openTime.doesNotContain=" + DEFAULT_OPEN_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByCloseTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where closeTime equals to
        defaultBranchFiltering("closeTime.equals=" + DEFAULT_CLOSE_TIME, "closeTime.equals=" + UPDATED_CLOSE_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByCloseTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where closeTime in
        defaultBranchFiltering("closeTime.in=" + DEFAULT_CLOSE_TIME + "," + UPDATED_CLOSE_TIME, "closeTime.in=" + UPDATED_CLOSE_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByCloseTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where closeTime is not null
        defaultBranchFiltering("closeTime.specified=true", "closeTime.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByCloseTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where closeTime contains
        defaultBranchFiltering("closeTime.contains=" + DEFAULT_CLOSE_TIME, "closeTime.contains=" + UPDATED_CLOSE_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByCloseTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where closeTime does not contain
        defaultBranchFiltering("closeTime.doesNotContain=" + UPDATED_CLOSE_TIME, "closeTime.doesNotContain=" + DEFAULT_CLOSE_TIME);
    }

    @Test
    @Transactional
    void getAllBranchesByLastBookingTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where lastBookingTime equals to
        defaultBranchFiltering(
            "lastBookingTime.equals=" + DEFAULT_LAST_BOOKING_TIME,
            "lastBookingTime.equals=" + UPDATED_LAST_BOOKING_TIME
        );
    }

    @Test
    @Transactional
    void getAllBranchesByLastBookingTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where lastBookingTime in
        defaultBranchFiltering(
            "lastBookingTime.in=" + DEFAULT_LAST_BOOKING_TIME + "," + UPDATED_LAST_BOOKING_TIME,
            "lastBookingTime.in=" + UPDATED_LAST_BOOKING_TIME
        );
    }

    @Test
    @Transactional
    void getAllBranchesByLastBookingTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where lastBookingTime is not null
        defaultBranchFiltering("lastBookingTime.specified=true", "lastBookingTime.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByLastBookingTimeContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where lastBookingTime contains
        defaultBranchFiltering(
            "lastBookingTime.contains=" + DEFAULT_LAST_BOOKING_TIME,
            "lastBookingTime.contains=" + UPDATED_LAST_BOOKING_TIME
        );
    }

    @Test
    @Transactional
    void getAllBranchesByLastBookingTimeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where lastBookingTime does not contain
        defaultBranchFiltering(
            "lastBookingTime.doesNotContain=" + UPDATED_LAST_BOOKING_TIME,
            "lastBookingTime.doesNotContain=" + DEFAULT_LAST_BOOKING_TIME
        );
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity equals to
        defaultBranchFiltering("capacity.equals=" + DEFAULT_CAPACITY, "capacity.equals=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity in
        defaultBranchFiltering("capacity.in=" + DEFAULT_CAPACITY + "," + UPDATED_CAPACITY, "capacity.in=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity is not null
        defaultBranchFiltering("capacity.specified=true", "capacity.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity is greater than or equal to
        defaultBranchFiltering("capacity.greaterThanOrEqual=" + DEFAULT_CAPACITY, "capacity.greaterThanOrEqual=" + UPDATED_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity is less than or equal to
        defaultBranchFiltering("capacity.lessThanOrEqual=" + DEFAULT_CAPACITY, "capacity.lessThanOrEqual=" + SMALLER_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity is less than
        defaultBranchFiltering("capacity.lessThan=" + UPDATED_CAPACITY, "capacity.lessThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByCapacityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where capacity is greater than
        defaultBranchFiltering("capacity.greaterThan=" + SMALLER_CAPACITY, "capacity.greaterThan=" + DEFAULT_CAPACITY);
    }

    @Test
    @Transactional
    void getAllBranchesByMapEmbedUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapEmbedUrl equals to
        defaultBranchFiltering("mapEmbedUrl.equals=" + DEFAULT_MAP_EMBED_URL, "mapEmbedUrl.equals=" + UPDATED_MAP_EMBED_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByMapEmbedUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapEmbedUrl in
        defaultBranchFiltering(
            "mapEmbedUrl.in=" + DEFAULT_MAP_EMBED_URL + "," + UPDATED_MAP_EMBED_URL,
            "mapEmbedUrl.in=" + UPDATED_MAP_EMBED_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByMapEmbedUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapEmbedUrl is not null
        defaultBranchFiltering("mapEmbedUrl.specified=true", "mapEmbedUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByMapEmbedUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapEmbedUrl contains
        defaultBranchFiltering("mapEmbedUrl.contains=" + DEFAULT_MAP_EMBED_URL, "mapEmbedUrl.contains=" + UPDATED_MAP_EMBED_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByMapEmbedUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapEmbedUrl does not contain
        defaultBranchFiltering(
            "mapEmbedUrl.doesNotContain=" + UPDATED_MAP_EMBED_URL,
            "mapEmbedUrl.doesNotContain=" + DEFAULT_MAP_EMBED_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByMapDirectionUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapDirectionUrl equals to
        defaultBranchFiltering(
            "mapDirectionUrl.equals=" + DEFAULT_MAP_DIRECTION_URL,
            "mapDirectionUrl.equals=" + UPDATED_MAP_DIRECTION_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByMapDirectionUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapDirectionUrl in
        defaultBranchFiltering(
            "mapDirectionUrl.in=" + DEFAULT_MAP_DIRECTION_URL + "," + UPDATED_MAP_DIRECTION_URL,
            "mapDirectionUrl.in=" + UPDATED_MAP_DIRECTION_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByMapDirectionUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapDirectionUrl is not null
        defaultBranchFiltering("mapDirectionUrl.specified=true", "mapDirectionUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByMapDirectionUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapDirectionUrl contains
        defaultBranchFiltering(
            "mapDirectionUrl.contains=" + DEFAULT_MAP_DIRECTION_URL,
            "mapDirectionUrl.contains=" + UPDATED_MAP_DIRECTION_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByMapDirectionUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where mapDirectionUrl does not contain
        defaultBranchFiltering(
            "mapDirectionUrl.doesNotContain=" + UPDATED_MAP_DIRECTION_URL,
            "mapDirectionUrl.doesNotContain=" + DEFAULT_MAP_DIRECTION_URL
        );
    }

    @Test
    @Transactional
    void getAllBranchesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where imageUrl equals to
        defaultBranchFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where imageUrl in
        defaultBranchFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where imageUrl is not null
        defaultBranchFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where imageUrl contains
        defaultBranchFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where imageUrl does not contain
        defaultBranchFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder equals to
        defaultBranchFiltering("displayOrder.equals=" + DEFAULT_DISPLAY_ORDER, "displayOrder.equals=" + UPDATED_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder in
        defaultBranchFiltering(
            "displayOrder.in=" + DEFAULT_DISPLAY_ORDER + "," + UPDATED_DISPLAY_ORDER,
            "displayOrder.in=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder is not null
        defaultBranchFiltering("displayOrder.specified=true", "displayOrder.specified=false");
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder is greater than or equal to
        defaultBranchFiltering(
            "displayOrder.greaterThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.greaterThanOrEqual=" + UPDATED_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder is less than or equal to
        defaultBranchFiltering(
            "displayOrder.lessThanOrEqual=" + DEFAULT_DISPLAY_ORDER,
            "displayOrder.lessThanOrEqual=" + SMALLER_DISPLAY_ORDER
        );
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder is less than
        defaultBranchFiltering("displayOrder.lessThan=" + UPDATED_DISPLAY_ORDER, "displayOrder.lessThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllBranchesByDisplayOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where displayOrder is greater than
        defaultBranchFiltering("displayOrder.greaterThan=" + SMALLER_DISPLAY_ORDER, "displayOrder.greaterThan=" + DEFAULT_DISPLAY_ORDER);
    }

    @Test
    @Transactional
    void getAllBranchesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where active equals to
        defaultBranchFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBranchesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where active in
        defaultBranchFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllBranchesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        // Get all the branchList where active is not null
        defaultBranchFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultBranchFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBranchShouldBeFound(shouldBeFound);
        defaultBranchShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBranchShouldBeFound(String filter) throws Exception {
        restBranchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branch.getId().intValue())))
            .andExpect(jsonPath("$.[*].nameVi").value(hasItem(DEFAULT_NAME_VI)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].addressVi").value(hasItem(DEFAULT_ADDRESS_VI)))
            .andExpect(jsonPath("$.[*].addressEn").value(hasItem(DEFAULT_ADDRESS_EN)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].whatsapp").value(hasItem(DEFAULT_WHATSAPP)))
            .andExpect(jsonPath("$.[*].zalo").value(hasItem(DEFAULT_ZALO)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].openTime").value(hasItem(DEFAULT_OPEN_TIME)))
            .andExpect(jsonPath("$.[*].closeTime").value(hasItem(DEFAULT_CLOSE_TIME)))
            .andExpect(jsonPath("$.[*].lastBookingTime").value(hasItem(DEFAULT_LAST_BOOKING_TIME)))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].mapEmbedUrl").value(hasItem(DEFAULT_MAP_EMBED_URL)))
            .andExpect(jsonPath("$.[*].mapDirectionUrl").value(hasItem(DEFAULT_MAP_DIRECTION_URL)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].displayOrder").value(hasItem(DEFAULT_DISPLAY_ORDER)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restBranchMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBranchShouldNotBeFound(String filter) throws Exception {
        restBranchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBranchMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBranch() throws Exception {
        // Get the branch
        restBranchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBranch() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branch
        Branch updatedBranch = branchRepository.findById(branch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBranch are not directly saved in db
        em.detach(updatedBranch);
        updatedBranch
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .addressVi(UPDATED_ADDRESS_VI)
            .addressEn(UPDATED_ADDRESS_EN)
            .phone(UPDATED_PHONE)
            .whatsapp(UPDATED_WHATSAPP)
            .zalo(UPDATED_ZALO)
            .email(UPDATED_EMAIL)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .lastBookingTime(UPDATED_LAST_BOOKING_TIME)
            .capacity(UPDATED_CAPACITY)
            .mapEmbedUrl(UPDATED_MAP_EMBED_URL)
            .mapDirectionUrl(UPDATED_MAP_DIRECTION_URL)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);
        BranchDTO branchDTO = branchMapper.toDto(updatedBranch);

        restBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, branchDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO))
            )
            .andExpect(status().isOk());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBranchToMatchAllProperties(updatedBranch);
    }

    @Test
    @Transactional
    void putNonExistingBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, branchDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(branchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBranchWithPatch() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branch using partial update
        Branch partialUpdatedBranch = new Branch();
        partialUpdatedBranch.setId(branch.getId());

        partialUpdatedBranch
            .nameVi(UPDATED_NAME_VI)
            .addressVi(UPDATED_ADDRESS_VI)
            .addressEn(UPDATED_ADDRESS_EN)
            .whatsapp(UPDATED_WHATSAPP)
            .zalo(UPDATED_ZALO)
            .lastBookingTime(UPDATED_LAST_BOOKING_TIME)
            .mapEmbedUrl(UPDATED_MAP_EMBED_URL)
            .mapDirectionUrl(UPDATED_MAP_DIRECTION_URL);

        restBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBranch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBranch))
            )
            .andExpect(status().isOk());

        // Validate the Branch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBranch, branch), getPersistedBranch(branch));
    }

    @Test
    @Transactional
    void fullUpdateBranchWithPatch() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the branch using partial update
        Branch partialUpdatedBranch = new Branch();
        partialUpdatedBranch.setId(branch.getId());

        partialUpdatedBranch
            .nameVi(UPDATED_NAME_VI)
            .nameEn(UPDATED_NAME_EN)
            .slug(UPDATED_SLUG)
            .addressVi(UPDATED_ADDRESS_VI)
            .addressEn(UPDATED_ADDRESS_EN)
            .phone(UPDATED_PHONE)
            .whatsapp(UPDATED_WHATSAPP)
            .zalo(UPDATED_ZALO)
            .email(UPDATED_EMAIL)
            .openTime(UPDATED_OPEN_TIME)
            .closeTime(UPDATED_CLOSE_TIME)
            .lastBookingTime(UPDATED_LAST_BOOKING_TIME)
            .capacity(UPDATED_CAPACITY)
            .mapEmbedUrl(UPDATED_MAP_EMBED_URL)
            .mapDirectionUrl(UPDATED_MAP_DIRECTION_URL)
            .imageUrl(UPDATED_IMAGE_URL)
            .displayOrder(UPDATED_DISPLAY_ORDER)
            .active(UPDATED_ACTIVE);

        restBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBranch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBranch))
            )
            .andExpect(status().isOk());

        // Validate the Branch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBranchUpdatableFieldsEquals(partialUpdatedBranch, getPersistedBranch(partialUpdatedBranch));
    }

    @Test
    @Transactional
    void patchNonExistingBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, branchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(branchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(branchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        branch.setId(longCount.incrementAndGet());

        // Create the Branch
        BranchDTO branchDTO = branchMapper.toDto(branch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBranchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(branchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Branch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBranch() throws Exception {
        // Initialize the database
        insertedBranch = branchRepository.saveAndFlush(branch);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the branch
        restBranchMockMvc
            .perform(delete(ENTITY_API_URL_ID, branch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return branchRepository.count();
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

    protected Branch getPersistedBranch(Branch branch) {
        return branchRepository.findById(branch.getId()).orElseThrow();
    }

    protected void assertPersistedBranchToMatchAllProperties(Branch expectedBranch) {
        assertBranchAllPropertiesEquals(expectedBranch, getPersistedBranch(expectedBranch));
    }

    protected void assertPersistedBranchToMatchUpdatableProperties(Branch expectedBranch) {
        assertBranchAllUpdatablePropertiesEquals(expectedBranch, getPersistedBranch(expectedBranch));
    }
}
