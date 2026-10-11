package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PromotionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Promotion;
import com.mycompany.myapp.repository.PromotionRepository;
import com.mycompany.myapp.service.dto.PromotionDTO;
import com.mycompany.myapp.service.mapper.PromotionMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PromotionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PromotionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DISCOUNT_PERCENT = 0;
    private static final Integer UPDATED_DISCOUNT_PERCENT = 1;
    private static final Integer SMALLER_DISCOUNT_PERCENT = 0 - 1;

    private static final BigDecimal DEFAULT_DISCOUNT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_DISCOUNT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal SMALLER_DISCOUNT_AMOUNT = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/promotions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPromotionMockMvc;

    private Promotion promotion;

    private Promotion insertedPromotion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createEntity() {
        return new Promotion()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .discountPercent(DEFAULT_DISCOUNT_PERCENT)
            .discountAmount(DEFAULT_DISCOUNT_AMOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Promotion createUpdatedEntity() {
        return new Promotion()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        promotion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPromotion != null) {
            promotionRepository.delete(insertedPromotion);
            insertedPromotion = null;
        }
    }

    @Test
    @Transactional
    void createPromotion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);
        var returnedPromotionDTO = om.readValue(
            restPromotionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PromotionDTO.class
        );

        // Validate the Promotion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPromotion = promotionMapper.toEntity(returnedPromotionDTO);
        assertPromotionUpdatableFieldsEquals(returnedPromotion, getPersistedPromotion(returnedPromotion));

        insertedPromotion = returnedPromotion;
    }

    @Test
    @Transactional
    void createPromotionWithExistingId() throws Exception {
        // Create the Promotion with an existing ID
        promotion.setId(1L);
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setCode(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setName(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        promotion.setActive(null);

        // Create the Promotion, which fails.
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        restPromotionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPromotions() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get the promotion
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL_ID, promotion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(promotion.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.discountPercent").value(DEFAULT_DISCOUNT_PERCENT))
            .andExpect(jsonPath("$.discountAmount").value(sameNumber(DEFAULT_DISCOUNT_AMOUNT)))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getPromotionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        Long id = promotion.getId();

        defaultPromotionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPromotionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPromotionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code equals to
        defaultPromotionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code in
        defaultPromotionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code is not null
        defaultPromotionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code contains
        defaultPromotionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where code does not contain
        defaultPromotionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name equals to
        defaultPromotionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name in
        defaultPromotionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name is not null
        defaultPromotionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name contains
        defaultPromotionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where name does not contain
        defaultPromotionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPromotionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where description equals to
        defaultPromotionFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPromotionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where description in
        defaultPromotionFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where description is not null
        defaultPromotionFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where description contains
        defaultPromotionFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPromotionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where description does not contain
        defaultPromotionFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent equals to
        defaultPromotionFiltering(
            "discountPercent.equals=" + DEFAULT_DISCOUNT_PERCENT,
            "discountPercent.equals=" + UPDATED_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent in
        defaultPromotionFiltering(
            "discountPercent.in=" + DEFAULT_DISCOUNT_PERCENT + "," + UPDATED_DISCOUNT_PERCENT,
            "discountPercent.in=" + UPDATED_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent is not null
        defaultPromotionFiltering("discountPercent.specified=true", "discountPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent is greater than or equal to
        defaultPromotionFiltering(
            "discountPercent.greaterThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT,
            "discountPercent.greaterThanOrEqual=" + (DEFAULT_DISCOUNT_PERCENT + 1)
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent is less than or equal to
        defaultPromotionFiltering(
            "discountPercent.lessThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT,
            "discountPercent.lessThanOrEqual=" + SMALLER_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent is less than
        defaultPromotionFiltering(
            "discountPercent.lessThan=" + (DEFAULT_DISCOUNT_PERCENT + 1),
            "discountPercent.lessThan=" + DEFAULT_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountPercent is greater than
        defaultPromotionFiltering(
            "discountPercent.greaterThan=" + SMALLER_DISCOUNT_PERCENT,
            "discountPercent.greaterThan=" + DEFAULT_DISCOUNT_PERCENT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount equals to
        defaultPromotionFiltering("discountAmount.equals=" + DEFAULT_DISCOUNT_AMOUNT, "discountAmount.equals=" + UPDATED_DISCOUNT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount in
        defaultPromotionFiltering(
            "discountAmount.in=" + DEFAULT_DISCOUNT_AMOUNT + "," + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.in=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount is not null
        defaultPromotionFiltering("discountAmount.specified=true", "discountAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount is greater than or equal to
        defaultPromotionFiltering(
            "discountAmount.greaterThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.greaterThanOrEqual=" + UPDATED_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount is less than or equal to
        defaultPromotionFiltering(
            "discountAmount.lessThanOrEqual=" + DEFAULT_DISCOUNT_AMOUNT,
            "discountAmount.lessThanOrEqual=" + SMALLER_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount is less than
        defaultPromotionFiltering(
            "discountAmount.lessThan=" + UPDATED_DISCOUNT_AMOUNT,
            "discountAmount.lessThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByDiscountAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where discountAmount is greater than
        defaultPromotionFiltering(
            "discountAmount.greaterThan=" + SMALLER_DISCOUNT_AMOUNT,
            "discountAmount.greaterThan=" + DEFAULT_DISCOUNT_AMOUNT
        );
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate equals to
        defaultPromotionFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate in
        defaultPromotionFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where startDate is not null
        defaultPromotionFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate equals to
        defaultPromotionFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate in
        defaultPromotionFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllPromotionsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where endDate is not null
        defaultPromotionFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPromotionsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where active equals to
        defaultPromotionFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where active in
        defaultPromotionFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllPromotionsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        // Get all the promotionList where active is not null
        defaultPromotionFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultPromotionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPromotionShouldBeFound(shouldBeFound);
        defaultPromotionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPromotionShouldBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(promotion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT)))
            .andExpect(jsonPath("$.[*].discountAmount").value(hasItem(sameNumber(DEFAULT_DISCOUNT_AMOUNT))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPromotionShouldNotBeFound(String filter) throws Exception {
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPromotionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPromotion() throws Exception {
        // Get the promotion
        restPromotionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion
        Promotion updatedPromotion = promotionRepository.findById(promotion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPromotion are not directly saved in db
        em.detach(updatedPromotion);
        updatedPromotion
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE);
        PromotionDTO promotionDTO = promotionMapper.toDto(updatedPromotion);

        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPromotionToMatchAllProperties(updatedPromotion);
    }

    @Test
    @Transactional
    void putNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion.name(UPDATED_NAME).discountPercent(UPDATED_DISCOUNT_PERCENT).startDate(UPDATED_START_DATE);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPromotion, promotion),
            getPersistedPromotion(promotion)
        );
    }

    @Test
    @Transactional
    void fullUpdatePromotionWithPatch() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the promotion using partial update
        Promotion partialUpdatedPromotion = new Promotion();
        partialUpdatedPromotion.setId(promotion.getId());

        partialUpdatedPromotion
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .discountAmount(UPDATED_DISCOUNT_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .active(UPDATED_ACTIVE);

        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPromotion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPromotion))
            )
            .andExpect(status().isOk());

        // Validate the Promotion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPromotionUpdatableFieldsEquals(partialUpdatedPromotion, getPersistedPromotion(partialUpdatedPromotion));
    }

    @Test
    @Transactional
    void patchNonExistingPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, promotionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(promotionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPromotion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        promotion.setId(longCount.incrementAndGet());

        // Create the Promotion
        PromotionDTO promotionDTO = promotionMapper.toDto(promotion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPromotionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(promotionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Promotion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePromotion() throws Exception {
        // Initialize the database
        insertedPromotion = promotionRepository.saveAndFlush(promotion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the promotion
        restPromotionMockMvc
            .perform(delete(ENTITY_API_URL_ID, promotion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return promotionRepository.count();
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

    protected Promotion getPersistedPromotion(Promotion promotion) {
        return promotionRepository.findById(promotion.getId()).orElseThrow();
    }

    protected void assertPersistedPromotionToMatchAllProperties(Promotion expectedPromotion) {
        assertPromotionAllPropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }

    protected void assertPersistedPromotionToMatchUpdatableProperties(Promotion expectedPromotion) {
        assertPromotionAllUpdatablePropertiesEquals(expectedPromotion, getPersistedPromotion(expectedPromotion));
    }
}
