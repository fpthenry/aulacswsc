package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ReviewAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Review;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.repository.ReviewRepository;
import com.mycompany.myapp.service.ReviewService;
import com.mycompany.myapp.service.dto.ReviewDTO;
import com.mycompany.myapp.service.mapper.ReviewMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReviewResourceIT {

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO_URL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_REVIEW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REVIEW_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_REVIEW_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final Boolean DEFAULT_FEATURED = false;
    private static final Boolean UPDATED_FEATURED = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewRepository reviewRepositoryMock;

    @Autowired
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewService reviewServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReviewMockMvc;

    private Review review;

    private Review insertedReview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createEntity() {
        return new Review()
            .authorName(DEFAULT_AUTHOR_NAME)
            .rating(DEFAULT_RATING)
            .comment(DEFAULT_COMMENT)
            .videoUrl(DEFAULT_VIDEO_URL)
            .reviewDate(DEFAULT_REVIEW_DATE)
            .approved(DEFAULT_APPROVED)
            .featured(DEFAULT_FEATURED)
            .createdDate(DEFAULT_CREATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createUpdatedEntity() {
        return new Review()
            .authorName(UPDATED_AUTHOR_NAME)
            .rating(UPDATED_RATING)
            .comment(UPDATED_COMMENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .reviewDate(UPDATED_REVIEW_DATE)
            .approved(UPDATED_APPROVED)
            .featured(UPDATED_FEATURED)
            .createdDate(UPDATED_CREATED_DATE);
    }

    @BeforeEach
    void initTest() {
        review = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReview != null) {
            reviewRepository.delete(insertedReview);
            insertedReview = null;
        }
    }

    @Test
    @Transactional
    void createReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);
        var returnedReviewDTO = om.readValue(
            restReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReviewDTO.class
        );

        // Validate the Review in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReview = reviewMapper.toEntity(returnedReviewDTO);
        assertReviewUpdatableFieldsEquals(returnedReview, getPersistedReview(returnedReview));

        insertedReview = returnedReview;
    }

    @Test
    @Transactional
    void createReviewWithExistingId() throws Exception {
        // Create the Review with an existing ID
        review.setId(1L);
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAuthorNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setAuthorName(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        review.setRating(null);

        // Create the Review, which fails.
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReviews() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())))
            .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED)))
            .andExpect(jsonPath("$.[*].featured").value(hasItem(DEFAULT_FEATURED)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReviewsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReviewsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReviewMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reviewRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(review.getId().intValue()))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.videoUrl").value(DEFAULT_VIDEO_URL))
            .andExpect(jsonPath("$.reviewDate").value(DEFAULT_REVIEW_DATE.toString()))
            .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED))
            .andExpect(jsonPath("$.featured").value(DEFAULT_FEATURED))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        Long id = review.getId();

        defaultReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where authorName equals to
        defaultReviewFiltering("authorName.equals=" + DEFAULT_AUTHOR_NAME, "authorName.equals=" + UPDATED_AUTHOR_NAME);
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where authorName in
        defaultReviewFiltering("authorName.in=" + DEFAULT_AUTHOR_NAME + "," + UPDATED_AUTHOR_NAME, "authorName.in=" + UPDATED_AUTHOR_NAME);
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where authorName is not null
        defaultReviewFiltering("authorName.specified=true", "authorName.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorNameContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where authorName contains
        defaultReviewFiltering("authorName.contains=" + DEFAULT_AUTHOR_NAME, "authorName.contains=" + UPDATED_AUTHOR_NAME);
    }

    @Test
    @Transactional
    void getAllReviewsByAuthorNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where authorName does not contain
        defaultReviewFiltering("authorName.doesNotContain=" + UPDATED_AUTHOR_NAME, "authorName.doesNotContain=" + DEFAULT_AUTHOR_NAME);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating equals to
        defaultReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating in
        defaultReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is not null
        defaultReviewFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is greater than or equal to
        defaultReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is less than or equal to
        defaultReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is less than
        defaultReviewFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where rating is greater than
        defaultReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where comment equals to
        defaultReviewFiltering("comment.equals=" + DEFAULT_COMMENT, "comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllReviewsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where comment in
        defaultReviewFiltering("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT, "comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllReviewsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where comment is not null
        defaultReviewFiltering("comment.specified=true", "comment.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByCommentContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where comment contains
        defaultReviewFiltering("comment.contains=" + DEFAULT_COMMENT, "comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void getAllReviewsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where comment does not contain
        defaultReviewFiltering("comment.doesNotContain=" + UPDATED_COMMENT, "comment.doesNotContain=" + DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    void getAllReviewsByVideoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where videoUrl equals to
        defaultReviewFiltering("videoUrl.equals=" + DEFAULT_VIDEO_URL, "videoUrl.equals=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllReviewsByVideoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where videoUrl in
        defaultReviewFiltering("videoUrl.in=" + DEFAULT_VIDEO_URL + "," + UPDATED_VIDEO_URL, "videoUrl.in=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllReviewsByVideoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where videoUrl is not null
        defaultReviewFiltering("videoUrl.specified=true", "videoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByVideoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where videoUrl contains
        defaultReviewFiltering("videoUrl.contains=" + DEFAULT_VIDEO_URL, "videoUrl.contains=" + UPDATED_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllReviewsByVideoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where videoUrl does not contain
        defaultReviewFiltering("videoUrl.doesNotContain=" + UPDATED_VIDEO_URL, "videoUrl.doesNotContain=" + DEFAULT_VIDEO_URL);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate equals to
        defaultReviewFiltering("reviewDate.equals=" + DEFAULT_REVIEW_DATE, "reviewDate.equals=" + UPDATED_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate in
        defaultReviewFiltering("reviewDate.in=" + DEFAULT_REVIEW_DATE + "," + UPDATED_REVIEW_DATE, "reviewDate.in=" + UPDATED_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is not null
        defaultReviewFiltering("reviewDate.specified=true", "reviewDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is greater than or equal to
        defaultReviewFiltering(
            "reviewDate.greaterThanOrEqual=" + DEFAULT_REVIEW_DATE,
            "reviewDate.greaterThanOrEqual=" + UPDATED_REVIEW_DATE
        );
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is less than or equal to
        defaultReviewFiltering("reviewDate.lessThanOrEqual=" + DEFAULT_REVIEW_DATE, "reviewDate.lessThanOrEqual=" + SMALLER_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is less than
        defaultReviewFiltering("reviewDate.lessThan=" + UPDATED_REVIEW_DATE, "reviewDate.lessThan=" + DEFAULT_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByReviewDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where reviewDate is greater than
        defaultReviewFiltering("reviewDate.greaterThan=" + SMALLER_REVIEW_DATE, "reviewDate.greaterThan=" + DEFAULT_REVIEW_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByApprovedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where approved equals to
        defaultReviewFiltering("approved.equals=" + DEFAULT_APPROVED, "approved.equals=" + UPDATED_APPROVED);
    }

    @Test
    @Transactional
    void getAllReviewsByApprovedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where approved in
        defaultReviewFiltering("approved.in=" + DEFAULT_APPROVED + "," + UPDATED_APPROVED, "approved.in=" + UPDATED_APPROVED);
    }

    @Test
    @Transactional
    void getAllReviewsByApprovedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where approved is not null
        defaultReviewFiltering("approved.specified=true", "approved.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByFeaturedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where featured equals to
        defaultReviewFiltering("featured.equals=" + DEFAULT_FEATURED, "featured.equals=" + UPDATED_FEATURED);
    }

    @Test
    @Transactional
    void getAllReviewsByFeaturedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where featured in
        defaultReviewFiltering("featured.in=" + DEFAULT_FEATURED + "," + UPDATED_FEATURED, "featured.in=" + UPDATED_FEATURED);
    }

    @Test
    @Transactional
    void getAllReviewsByFeaturedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where featured is not null
        defaultReviewFiltering("featured.specified=true", "featured.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdDate equals to
        defaultReviewFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdDate in
        defaultReviewFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        // Get all the reviewList where createdDate is not null
        defaultReviewFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            customer = CustomerResourceIT.createEntity();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        review.setCustomer(customer);
        reviewRepository.saveAndFlush(review);
        Long customerId = customer.getId();
        // Get all the reviewList where customer equals to customerId
        defaultReviewShouldBeFound("customerId.equals=" + customerId);

        // Get all the reviewList where customer equals to (customerId + 1)
        defaultReviewShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByServiceIsEqualToSomething() throws Exception {
        SpaService service;
        if (TestUtil.findAll(em, SpaService.class).isEmpty()) {
            reviewRepository.saveAndFlush(review);
            service = SpaServiceResourceIT.createEntity(em);
        } else {
            service = TestUtil.findAll(em, SpaService.class).get(0);
        }
        em.persist(service);
        em.flush();
        review.setService(service);
        reviewRepository.saveAndFlush(review);
        Long serviceId = service.getId();
        // Get all the reviewList where service equals to serviceId
        defaultReviewShouldBeFound("serviceId.equals=" + serviceId);

        // Get all the reviewList where service equals to (serviceId + 1)
        defaultReviewShouldNotBeFound("serviceId.equals=" + (serviceId + 1));
    }

    private void defaultReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReviewShouldBeFound(shouldBeFound);
        defaultReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReviewShouldBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(review.getId().intValue())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].videoUrl").value(hasItem(DEFAULT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].reviewDate").value(hasItem(DEFAULT_REVIEW_DATE.toString())))
            .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED)))
            .andExpect(jsonPath("$.[*].featured").value(hasItem(DEFAULT_FEATURED)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));

        // Check, that the count call also returns 1
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReviewShouldNotBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReview are not directly saved in db
        em.detach(updatedReview);
        updatedReview
            .authorName(UPDATED_AUTHOR_NAME)
            .rating(UPDATED_RATING)
            .comment(UPDATED_COMMENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .reviewDate(UPDATED_REVIEW_DATE)
            .approved(UPDATED_APPROVED)
            .featured(UPDATED_FEATURED)
            .createdDate(UPDATED_CREATED_DATE);
        ReviewDTO reviewDTO = reviewMapper.toDto(updatedReview);

        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReviewToMatchAllProperties(updatedReview);
    }

    @Test
    @Transactional
    void putNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview
            .authorName(UPDATED_AUTHOR_NAME)
            .videoUrl(UPDATED_VIDEO_URL)
            .approved(UPDATED_APPROVED)
            .createdDate(UPDATED_CREATED_DATE);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReview, review), getPersistedReview(review));
    }

    @Test
    @Transactional
    void fullUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview
            .authorName(UPDATED_AUTHOR_NAME)
            .rating(UPDATED_RATING)
            .comment(UPDATED_COMMENT)
            .videoUrl(UPDATED_VIDEO_URL)
            .reviewDate(UPDATED_REVIEW_DATE)
            .approved(UPDATED_APPROVED)
            .featured(UPDATED_FEATURED)
            .createdDate(UPDATED_CREATED_DATE);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReview.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReview))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewUpdatableFieldsEquals(partialUpdatedReview, getPersistedReview(partialUpdatedReview));
    }

    @Test
    @Transactional
    void patchNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reviewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        review.setId(longCount.incrementAndGet());

        // Create the Review
        ReviewDTO reviewDTO = reviewMapper.toDto(review);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reviewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReview() throws Exception {
        // Initialize the database
        insertedReview = reviewRepository.saveAndFlush(review);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the review
        restReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, review.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reviewRepository.count();
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

    protected Review getPersistedReview(Review review) {
        return reviewRepository.findById(review.getId()).orElseThrow();
    }

    protected void assertPersistedReviewToMatchAllProperties(Review expectedReview) {
        assertReviewAllPropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }

    protected void assertPersistedReviewToMatchUpdatableProperties(Review expectedReview) {
        assertReviewAllUpdatablePropertiesEquals(expectedReview, getPersistedReview(expectedReview));
    }
}
