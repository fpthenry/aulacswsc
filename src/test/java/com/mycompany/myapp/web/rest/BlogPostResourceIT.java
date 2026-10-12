package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BlogPostAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BlogPost;
import com.mycompany.myapp.domain.enumeration.BlogCategory;
import com.mycompany.myapp.repository.BlogPostRepository;
import com.mycompany.myapp.service.dto.BlogPostDTO;
import com.mycompany.myapp.service.mapper.BlogPostMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link BlogPostResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BlogPostResourceIT {

    private static final String DEFAULT_TITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_EXCERPT_VI = "AAAAAAAAAA";
    private static final String UPDATED_EXCERPT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_EXCERPT_EN = "AAAAAAAAAA";
    private static final String UPDATED_EXCERPT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_VI = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_EN = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final BlogCategory DEFAULT_CATEGORY = BlogCategory.NEWS;
    private static final BlogCategory UPDATED_CATEGORY = BlogCategory.EVENT;

    private static final Boolean DEFAULT_PUBLISHED = false;
    private static final Boolean UPDATED_PUBLISHED = true;

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/blog-posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlogPostMockMvc;

    private BlogPost blogPost;

    private BlogPost insertedBlogPost;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createEntity() {
        return new BlogPost()
            .titleVi(DEFAULT_TITLE_VI)
            .titleEn(DEFAULT_TITLE_EN)
            .slug(DEFAULT_SLUG)
            .excerptVi(DEFAULT_EXCERPT_VI)
            .excerptEn(DEFAULT_EXCERPT_EN)
            .contentVi(DEFAULT_CONTENT_VI)
            .contentEn(DEFAULT_CONTENT_EN)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .category(DEFAULT_CATEGORY)
            .published(DEFAULT_PUBLISHED)
            .publishedAt(DEFAULT_PUBLISHED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createUpdatedEntity() {
        return new BlogPost()
            .titleVi(UPDATED_TITLE_VI)
            .titleEn(UPDATED_TITLE_EN)
            .slug(UPDATED_SLUG)
            .excerptVi(UPDATED_EXCERPT_VI)
            .excerptEn(UPDATED_EXCERPT_EN)
            .contentVi(UPDATED_CONTENT_VI)
            .contentEn(UPDATED_CONTENT_EN)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .category(UPDATED_CATEGORY)
            .published(UPDATED_PUBLISHED)
            .publishedAt(UPDATED_PUBLISHED_AT);
    }

    @BeforeEach
    void initTest() {
        blogPost = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBlogPost != null) {
            blogPostRepository.delete(insertedBlogPost);
            insertedBlogPost = null;
        }
    }

    @Test
    @Transactional
    void createBlogPost() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);
        var returnedBlogPostDTO = om.readValue(
            restBlogPostMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BlogPostDTO.class
        );

        // Validate the BlogPost in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBlogPost = blogPostMapper.toEntity(returnedBlogPostDTO);
        assertBlogPostUpdatableFieldsEquals(returnedBlogPost, getPersistedBlogPost(returnedBlogPost));

        insertedBlogPost = returnedBlogPost;
    }

    @Test
    @Transactional
    void createBlogPostWithExistingId() throws Exception {
        // Create the BlogPost with an existing ID
        blogPost.setId(1L);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleViIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setTitleVi(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setSlug(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setPublished(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBlogPosts() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleVi").value(hasItem(DEFAULT_TITLE_VI)))
            .andExpect(jsonPath("$.[*].titleEn").value(hasItem(DEFAULT_TITLE_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].excerptVi").value(hasItem(DEFAULT_EXCERPT_VI)))
            .andExpect(jsonPath("$.[*].excerptEn").value(hasItem(DEFAULT_EXCERPT_EN)))
            .andExpect(jsonPath("$.[*].contentVi").value(hasItem(DEFAULT_CONTENT_VI)))
            .andExpect(jsonPath("$.[*].contentEn").value(hasItem(DEFAULT_CONTENT_EN)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)))
            .andExpect(jsonPath("$.[*].publishedAt").value(hasItem(DEFAULT_PUBLISHED_AT.toString())));
    }

    @Test
    @Transactional
    void getBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get the blogPost
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL_ID, blogPost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blogPost.getId().intValue()))
            .andExpect(jsonPath("$.titleVi").value(DEFAULT_TITLE_VI))
            .andExpect(jsonPath("$.titleEn").value(DEFAULT_TITLE_EN))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.excerptVi").value(DEFAULT_EXCERPT_VI))
            .andExpect(jsonPath("$.excerptEn").value(DEFAULT_EXCERPT_EN))
            .andExpect(jsonPath("$.contentVi").value(DEFAULT_CONTENT_VI))
            .andExpect(jsonPath("$.contentEn").value(DEFAULT_CONTENT_EN))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED))
            .andExpect(jsonPath("$.publishedAt").value(DEFAULT_PUBLISHED_AT.toString()));
    }

    @Test
    @Transactional
    void getBlogPostsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        Long id = blogPost.getId();

        defaultBlogPostFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBlogPostFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBlogPostFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleVi equals to
        defaultBlogPostFiltering("titleVi.equals=" + DEFAULT_TITLE_VI, "titleVi.equals=" + UPDATED_TITLE_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleVi in
        defaultBlogPostFiltering("titleVi.in=" + DEFAULT_TITLE_VI + "," + UPDATED_TITLE_VI, "titleVi.in=" + UPDATED_TITLE_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleVi is not null
        defaultBlogPostFiltering("titleVi.specified=true", "titleVi.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleViContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleVi contains
        defaultBlogPostFiltering("titleVi.contains=" + DEFAULT_TITLE_VI, "titleVi.contains=" + UPDATED_TITLE_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleVi does not contain
        defaultBlogPostFiltering("titleVi.doesNotContain=" + UPDATED_TITLE_VI, "titleVi.doesNotContain=" + DEFAULT_TITLE_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleEn equals to
        defaultBlogPostFiltering("titleEn.equals=" + DEFAULT_TITLE_EN, "titleEn.equals=" + UPDATED_TITLE_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleEn in
        defaultBlogPostFiltering("titleEn.in=" + DEFAULT_TITLE_EN + "," + UPDATED_TITLE_EN, "titleEn.in=" + UPDATED_TITLE_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleEn is not null
        defaultBlogPostFiltering("titleEn.specified=true", "titleEn.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleEnContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleEn contains
        defaultBlogPostFiltering("titleEn.contains=" + DEFAULT_TITLE_EN, "titleEn.contains=" + UPDATED_TITLE_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where titleEn does not contain
        defaultBlogPostFiltering("titleEn.doesNotContain=" + UPDATED_TITLE_EN, "titleEn.doesNotContain=" + DEFAULT_TITLE_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug equals to
        defaultBlogPostFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug in
        defaultBlogPostFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug is not null
        defaultBlogPostFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug contains
        defaultBlogPostFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug does not contain
        defaultBlogPostFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptViIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptVi equals to
        defaultBlogPostFiltering("excerptVi.equals=" + DEFAULT_EXCERPT_VI, "excerptVi.equals=" + UPDATED_EXCERPT_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptViIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptVi in
        defaultBlogPostFiltering("excerptVi.in=" + DEFAULT_EXCERPT_VI + "," + UPDATED_EXCERPT_VI, "excerptVi.in=" + UPDATED_EXCERPT_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptViIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptVi is not null
        defaultBlogPostFiltering("excerptVi.specified=true", "excerptVi.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptViContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptVi contains
        defaultBlogPostFiltering("excerptVi.contains=" + DEFAULT_EXCERPT_VI, "excerptVi.contains=" + UPDATED_EXCERPT_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptViNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptVi does not contain
        defaultBlogPostFiltering("excerptVi.doesNotContain=" + UPDATED_EXCERPT_VI, "excerptVi.doesNotContain=" + DEFAULT_EXCERPT_VI);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptEn equals to
        defaultBlogPostFiltering("excerptEn.equals=" + DEFAULT_EXCERPT_EN, "excerptEn.equals=" + UPDATED_EXCERPT_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptEn in
        defaultBlogPostFiltering("excerptEn.in=" + DEFAULT_EXCERPT_EN + "," + UPDATED_EXCERPT_EN, "excerptEn.in=" + UPDATED_EXCERPT_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptEn is not null
        defaultBlogPostFiltering("excerptEn.specified=true", "excerptEn.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptEnContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptEn contains
        defaultBlogPostFiltering("excerptEn.contains=" + DEFAULT_EXCERPT_EN, "excerptEn.contains=" + UPDATED_EXCERPT_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByExcerptEnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where excerptEn does not contain
        defaultBlogPostFiltering("excerptEn.doesNotContain=" + UPDATED_EXCERPT_EN, "excerptEn.doesNotContain=" + DEFAULT_EXCERPT_EN);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl equals to
        defaultBlogPostFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl in
        defaultBlogPostFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl is not null
        defaultBlogPostFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl contains
        defaultBlogPostFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl does not contain
        defaultBlogPostFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where category equals to
        defaultBlogPostFiltering("category.equals=" + DEFAULT_CATEGORY, "category.equals=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where category in
        defaultBlogPostFiltering("category.in=" + DEFAULT_CATEGORY + "," + UPDATED_CATEGORY, "category.in=" + UPDATED_CATEGORY);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where category is not null
        defaultBlogPostFiltering("category.specified=true", "category.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where published equals to
        defaultBlogPostFiltering("published.equals=" + DEFAULT_PUBLISHED, "published.equals=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where published in
        defaultBlogPostFiltering("published.in=" + DEFAULT_PUBLISHED + "," + UPDATED_PUBLISHED, "published.in=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where published is not null
        defaultBlogPostFiltering("published.specified=true", "published.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishedAt equals to
        defaultBlogPostFiltering("publishedAt.equals=" + DEFAULT_PUBLISHED_AT, "publishedAt.equals=" + UPDATED_PUBLISHED_AT);
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishedAt in
        defaultBlogPostFiltering(
            "publishedAt.in=" + DEFAULT_PUBLISHED_AT + "," + UPDATED_PUBLISHED_AT,
            "publishedAt.in=" + UPDATED_PUBLISHED_AT
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishedAt is not null
        defaultBlogPostFiltering("publishedAt.specified=true", "publishedAt.specified=false");
    }

    private void defaultBlogPostFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBlogPostShouldBeFound(shouldBeFound);
        defaultBlogPostShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBlogPostShouldBeFound(String filter) throws Exception {
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleVi").value(hasItem(DEFAULT_TITLE_VI)))
            .andExpect(jsonPath("$.[*].titleEn").value(hasItem(DEFAULT_TITLE_EN)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].excerptVi").value(hasItem(DEFAULT_EXCERPT_VI)))
            .andExpect(jsonPath("$.[*].excerptEn").value(hasItem(DEFAULT_EXCERPT_EN)))
            .andExpect(jsonPath("$.[*].contentVi").value(hasItem(DEFAULT_CONTENT_VI)))
            .andExpect(jsonPath("$.[*].contentEn").value(hasItem(DEFAULT_CONTENT_EN)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED)))
            .andExpect(jsonPath("$.[*].publishedAt").value(hasItem(DEFAULT_PUBLISHED_AT.toString())));

        // Check, that the count call also returns 1
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBlogPostShouldNotBeFound(String filter) throws Exception {
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBlogPost() throws Exception {
        // Get the blogPost
        restBlogPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost
        BlogPost updatedBlogPost = blogPostRepository.findById(blogPost.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBlogPost are not directly saved in db
        em.detach(updatedBlogPost);
        updatedBlogPost
            .titleVi(UPDATED_TITLE_VI)
            .titleEn(UPDATED_TITLE_EN)
            .slug(UPDATED_SLUG)
            .excerptVi(UPDATED_EXCERPT_VI)
            .excerptEn(UPDATED_EXCERPT_EN)
            .contentVi(UPDATED_CONTENT_VI)
            .contentEn(UPDATED_CONTENT_EN)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .category(UPDATED_CATEGORY)
            .published(UPDATED_PUBLISHED)
            .publishedAt(UPDATED_PUBLISHED_AT);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(updatedBlogPost);

        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBlogPostToMatchAllProperties(updatedBlogPost);
    }

    @Test
    @Transactional
    void putNonExistingBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlogPostWithPatch() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost using partial update
        BlogPost partialUpdatedBlogPost = new BlogPost();
        partialUpdatedBlogPost.setId(blogPost.getId());

        partialUpdatedBlogPost
            .slug(UPDATED_SLUG)
            .excerptVi(UPDATED_EXCERPT_VI)
            .excerptEn(UPDATED_EXCERPT_EN)
            .contentVi(UPDATED_CONTENT_VI)
            .contentEn(UPDATED_CONTENT_EN)
            .category(UPDATED_CATEGORY)
            .publishedAt(UPDATED_PUBLISHED_AT);

        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlogPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlogPost))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlogPostUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBlogPost, blogPost), getPersistedBlogPost(blogPost));
    }

    @Test
    @Transactional
    void fullUpdateBlogPostWithPatch() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost using partial update
        BlogPost partialUpdatedBlogPost = new BlogPost();
        partialUpdatedBlogPost.setId(blogPost.getId());

        partialUpdatedBlogPost
            .titleVi(UPDATED_TITLE_VI)
            .titleEn(UPDATED_TITLE_EN)
            .slug(UPDATED_SLUG)
            .excerptVi(UPDATED_EXCERPT_VI)
            .excerptEn(UPDATED_EXCERPT_EN)
            .contentVi(UPDATED_CONTENT_VI)
            .contentEn(UPDATED_CONTENT_EN)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .category(UPDATED_CATEGORY)
            .published(UPDATED_PUBLISHED)
            .publishedAt(UPDATED_PUBLISHED_AT);

        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlogPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlogPost))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlogPostUpdatableFieldsEquals(partialUpdatedBlogPost, getPersistedBlogPost(partialUpdatedBlogPost));
    }

    @Test
    @Transactional
    void patchNonExistingBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the blogPost
        restBlogPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, blogPost.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return blogPostRepository.count();
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

    protected BlogPost getPersistedBlogPost(BlogPost blogPost) {
        return blogPostRepository.findById(blogPost.getId()).orElseThrow();
    }

    protected void assertPersistedBlogPostToMatchAllProperties(BlogPost expectedBlogPost) {
        assertBlogPostAllPropertiesEquals(expectedBlogPost, getPersistedBlogPost(expectedBlogPost));
    }

    protected void assertPersistedBlogPostToMatchUpdatableProperties(BlogPost expectedBlogPost) {
        assertBlogPostAllUpdatablePropertiesEquals(expectedBlogPost, getPersistedBlogPost(expectedBlogPost));
    }
}
