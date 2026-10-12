package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SiteSettingAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SiteSetting;
import com.mycompany.myapp.repository.SiteSettingRepository;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import com.mycompany.myapp.service.mapper.SiteSettingMapper;
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
 * Integration tests for the {@link SiteSettingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SiteSettingResourceIT {

    private static final String DEFAULT_SITE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SITE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN_VI = "AAAAAAAAAA";
    private static final String UPDATED_SLOGAN_VI = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN_EN = "AAAAAAAAAA";
    private static final String UPDATED_SLOGAN_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HERO_SUBTITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_HERO_SUBTITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HERO_SUBTITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_HERO_SUBTITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HERO_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_HERO_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_HERO_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_HERO_VIDEO_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_HERO_VIDEO_ENABLED = false;
    private static final Boolean UPDATED_HERO_VIDEO_ENABLED = true;

    private static final String DEFAULT_BACKGROUND_MUSIC_URL = "AAAAAAAAAA";
    private static final String UPDATED_BACKGROUND_MUSIC_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ABOUT_TITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_TITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_ABOUT_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_ABOUT_CONTENT_VI = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_CONTENT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_ABOUT_CONTENT_EN = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_CONTENT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_1_TITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_1_TITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_1_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_1_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_1_TEXT_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_1_TEXT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_1_TEXT_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_1_TEXT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_2_TITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_2_TITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_2_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_2_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_2_TEXT_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_2_TEXT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_2_TEXT_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_2_TEXT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_3_TITLE_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_3_TITLE_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_3_TITLE_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_3_TITLE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_3_TEXT_VI = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_3_TEXT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_HIGHLIGHT_3_TEXT_EN = "AAAAAAAAAA";
    private static final String UPDATED_HIGHLIGHT_3_TEXT_EN = "BBBBBBBBBB";

    private static final String DEFAULT_HOTLINE = "AAAAAAAAAA";
    private static final String UPDATED_HOTLINE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_WHATSAPP_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_WHATSAPP_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ZALO_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ZALO_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_MESSENGER_URL = "AAAAAAAAAA";
    private static final String UPDATED_MESSENGER_URL = "BBBBBBBBBB";

    private static final String DEFAULT_FACEBOOK_URL = "AAAAAAAAAA";
    private static final String UPDATED_FACEBOOK_URL = "BBBBBBBBBB";

    private static final String DEFAULT_INSTAGRAM_URL = "AAAAAAAAAA";
    private static final String UPDATED_INSTAGRAM_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TIKTOK_URL = "AAAAAAAAAA";
    private static final String UPDATED_TIKTOK_URL = "BBBBBBBBBB";

    private static final String DEFAULT_YOUTUBE_URL = "AAAAAAAAAA";
    private static final String UPDATED_YOUTUBE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_OPENING_HOURS_VI = "AAAAAAAAAA";
    private static final String UPDATED_OPENING_HOURS_VI = "BBBBBBBBBB";

    private static final String DEFAULT_OPENING_HOURS_EN = "AAAAAAAAAA";
    private static final String UPDATED_OPENING_HOURS_EN = "BBBBBBBBBB";

    private static final String DEFAULT_META_DESCRIPTION_VI = "AAAAAAAAAA";
    private static final String UPDATED_META_DESCRIPTION_VI = "BBBBBBBBBB";

    private static final String DEFAULT_META_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_META_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final String DEFAULT_POLICY_CONTENT_VI = "AAAAAAAAAA";
    private static final String UPDATED_POLICY_CONTENT_VI = "BBBBBBBBBB";

    private static final String DEFAULT_POLICY_CONTENT_EN = "AAAAAAAAAA";
    private static final String UPDATED_POLICY_CONTENT_EN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/site-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SiteSettingRepository siteSettingRepository;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSiteSettingMockMvc;

    private SiteSetting siteSetting;

    private SiteSetting insertedSiteSetting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SiteSetting createEntity() {
        return new SiteSetting()
            .siteName(DEFAULT_SITE_NAME)
            .logoUrl(DEFAULT_LOGO_URL)
            .sloganVi(DEFAULT_SLOGAN_VI)
            .sloganEn(DEFAULT_SLOGAN_EN)
            .heroSubtitleVi(DEFAULT_HERO_SUBTITLE_VI)
            .heroSubtitleEn(DEFAULT_HERO_SUBTITLE_EN)
            .heroImageUrl(DEFAULT_HERO_IMAGE_URL)
            .heroVideoUrl(DEFAULT_HERO_VIDEO_URL)
            .heroVideoEnabled(DEFAULT_HERO_VIDEO_ENABLED)
            .backgroundMusicUrl(DEFAULT_BACKGROUND_MUSIC_URL)
            .aboutTitleVi(DEFAULT_ABOUT_TITLE_VI)
            .aboutTitleEn(DEFAULT_ABOUT_TITLE_EN)
            .aboutContentVi(DEFAULT_ABOUT_CONTENT_VI)
            .aboutContentEn(DEFAULT_ABOUT_CONTENT_EN)
            .highlight1TitleVi(DEFAULT_HIGHLIGHT_1_TITLE_VI)
            .highlight1TitleEn(DEFAULT_HIGHLIGHT_1_TITLE_EN)
            .highlight1TextVi(DEFAULT_HIGHLIGHT_1_TEXT_VI)
            .highlight1TextEn(DEFAULT_HIGHLIGHT_1_TEXT_EN)
            .highlight2TitleVi(DEFAULT_HIGHLIGHT_2_TITLE_VI)
            .highlight2TitleEn(DEFAULT_HIGHLIGHT_2_TITLE_EN)
            .highlight2TextVi(DEFAULT_HIGHLIGHT_2_TEXT_VI)
            .highlight2TextEn(DEFAULT_HIGHLIGHT_2_TEXT_EN)
            .highlight3TitleVi(DEFAULT_HIGHLIGHT_3_TITLE_VI)
            .highlight3TitleEn(DEFAULT_HIGHLIGHT_3_TITLE_EN)
            .highlight3TextVi(DEFAULT_HIGHLIGHT_3_TEXT_VI)
            .highlight3TextEn(DEFAULT_HIGHLIGHT_3_TEXT_EN)
            .hotline(DEFAULT_HOTLINE)
            .email(DEFAULT_EMAIL)
            .whatsappNumber(DEFAULT_WHATSAPP_NUMBER)
            .zaloNumber(DEFAULT_ZALO_NUMBER)
            .messengerUrl(DEFAULT_MESSENGER_URL)
            .facebookUrl(DEFAULT_FACEBOOK_URL)
            .instagramUrl(DEFAULT_INSTAGRAM_URL)
            .tiktokUrl(DEFAULT_TIKTOK_URL)
            .youtubeUrl(DEFAULT_YOUTUBE_URL)
            .openingHoursVi(DEFAULT_OPENING_HOURS_VI)
            .openingHoursEn(DEFAULT_OPENING_HOURS_EN)
            .metaDescriptionVi(DEFAULT_META_DESCRIPTION_VI)
            .metaDescriptionEn(DEFAULT_META_DESCRIPTION_EN)
            .policyContentVi(DEFAULT_POLICY_CONTENT_VI)
            .policyContentEn(DEFAULT_POLICY_CONTENT_EN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SiteSetting createUpdatedEntity() {
        return new SiteSetting()
            .siteName(UPDATED_SITE_NAME)
            .logoUrl(UPDATED_LOGO_URL)
            .sloganVi(UPDATED_SLOGAN_VI)
            .sloganEn(UPDATED_SLOGAN_EN)
            .heroSubtitleVi(UPDATED_HERO_SUBTITLE_VI)
            .heroSubtitleEn(UPDATED_HERO_SUBTITLE_EN)
            .heroImageUrl(UPDATED_HERO_IMAGE_URL)
            .heroVideoUrl(UPDATED_HERO_VIDEO_URL)
            .heroVideoEnabled(UPDATED_HERO_VIDEO_ENABLED)
            .backgroundMusicUrl(UPDATED_BACKGROUND_MUSIC_URL)
            .aboutTitleVi(UPDATED_ABOUT_TITLE_VI)
            .aboutTitleEn(UPDATED_ABOUT_TITLE_EN)
            .aboutContentVi(UPDATED_ABOUT_CONTENT_VI)
            .aboutContentEn(UPDATED_ABOUT_CONTENT_EN)
            .highlight1TitleVi(UPDATED_HIGHLIGHT_1_TITLE_VI)
            .highlight1TitleEn(UPDATED_HIGHLIGHT_1_TITLE_EN)
            .highlight1TextVi(UPDATED_HIGHLIGHT_1_TEXT_VI)
            .highlight1TextEn(UPDATED_HIGHLIGHT_1_TEXT_EN)
            .highlight2TitleVi(UPDATED_HIGHLIGHT_2_TITLE_VI)
            .highlight2TitleEn(UPDATED_HIGHLIGHT_2_TITLE_EN)
            .highlight2TextVi(UPDATED_HIGHLIGHT_2_TEXT_VI)
            .highlight2TextEn(UPDATED_HIGHLIGHT_2_TEXT_EN)
            .highlight3TitleVi(UPDATED_HIGHLIGHT_3_TITLE_VI)
            .highlight3TitleEn(UPDATED_HIGHLIGHT_3_TITLE_EN)
            .highlight3TextVi(UPDATED_HIGHLIGHT_3_TEXT_VI)
            .highlight3TextEn(UPDATED_HIGHLIGHT_3_TEXT_EN)
            .hotline(UPDATED_HOTLINE)
            .email(UPDATED_EMAIL)
            .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
            .zaloNumber(UPDATED_ZALO_NUMBER)
            .messengerUrl(UPDATED_MESSENGER_URL)
            .facebookUrl(UPDATED_FACEBOOK_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .tiktokUrl(UPDATED_TIKTOK_URL)
            .youtubeUrl(UPDATED_YOUTUBE_URL)
            .openingHoursVi(UPDATED_OPENING_HOURS_VI)
            .openingHoursEn(UPDATED_OPENING_HOURS_EN)
            .metaDescriptionVi(UPDATED_META_DESCRIPTION_VI)
            .metaDescriptionEn(UPDATED_META_DESCRIPTION_EN)
            .policyContentVi(UPDATED_POLICY_CONTENT_VI)
            .policyContentEn(UPDATED_POLICY_CONTENT_EN);
    }

    @BeforeEach
    void initTest() {
        siteSetting = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSiteSetting != null) {
            siteSettingRepository.delete(insertedSiteSetting);
            insertedSiteSetting = null;
        }
    }

    @Test
    @Transactional
    void createSiteSetting() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);
        var returnedSiteSettingDTO = om.readValue(
            restSiteSettingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteSettingDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SiteSettingDTO.class
        );

        // Validate the SiteSetting in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSiteSetting = siteSettingMapper.toEntity(returnedSiteSettingDTO);
        assertSiteSettingUpdatableFieldsEquals(returnedSiteSetting, getPersistedSiteSetting(returnedSiteSetting));

        insertedSiteSetting = returnedSiteSetting;
    }

    @Test
    @Transactional
    void createSiteSettingWithExistingId() throws Exception {
        // Create the SiteSetting with an existing ID
        siteSetting.setId(1L);
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteSettingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSiteNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        siteSetting.setSiteName(null);

        // Create the SiteSetting, which fails.
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        restSiteSettingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteSettingDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSiteSettings() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        // Get all the siteSettingList
        restSiteSettingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(siteSetting.getId().intValue())))
            .andExpect(jsonPath("$.[*].siteName").value(hasItem(DEFAULT_SITE_NAME)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].sloganVi").value(hasItem(DEFAULT_SLOGAN_VI)))
            .andExpect(jsonPath("$.[*].sloganEn").value(hasItem(DEFAULT_SLOGAN_EN)))
            .andExpect(jsonPath("$.[*].heroSubtitleVi").value(hasItem(DEFAULT_HERO_SUBTITLE_VI)))
            .andExpect(jsonPath("$.[*].heroSubtitleEn").value(hasItem(DEFAULT_HERO_SUBTITLE_EN)))
            .andExpect(jsonPath("$.[*].heroImageUrl").value(hasItem(DEFAULT_HERO_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].heroVideoUrl").value(hasItem(DEFAULT_HERO_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].heroVideoEnabled").value(hasItem(DEFAULT_HERO_VIDEO_ENABLED)))
            .andExpect(jsonPath("$.[*].backgroundMusicUrl").value(hasItem(DEFAULT_BACKGROUND_MUSIC_URL)))
            .andExpect(jsonPath("$.[*].aboutTitleVi").value(hasItem(DEFAULT_ABOUT_TITLE_VI)))
            .andExpect(jsonPath("$.[*].aboutTitleEn").value(hasItem(DEFAULT_ABOUT_TITLE_EN)))
            .andExpect(jsonPath("$.[*].aboutContentVi").value(hasItem(DEFAULT_ABOUT_CONTENT_VI)))
            .andExpect(jsonPath("$.[*].aboutContentEn").value(hasItem(DEFAULT_ABOUT_CONTENT_EN)))
            .andExpect(jsonPath("$.[*].highlight1TitleVi").value(hasItem(DEFAULT_HIGHLIGHT_1_TITLE_VI)))
            .andExpect(jsonPath("$.[*].highlight1TitleEn").value(hasItem(DEFAULT_HIGHLIGHT_1_TITLE_EN)))
            .andExpect(jsonPath("$.[*].highlight1TextVi").value(hasItem(DEFAULT_HIGHLIGHT_1_TEXT_VI)))
            .andExpect(jsonPath("$.[*].highlight1TextEn").value(hasItem(DEFAULT_HIGHLIGHT_1_TEXT_EN)))
            .andExpect(jsonPath("$.[*].highlight2TitleVi").value(hasItem(DEFAULT_HIGHLIGHT_2_TITLE_VI)))
            .andExpect(jsonPath("$.[*].highlight2TitleEn").value(hasItem(DEFAULT_HIGHLIGHT_2_TITLE_EN)))
            .andExpect(jsonPath("$.[*].highlight2TextVi").value(hasItem(DEFAULT_HIGHLIGHT_2_TEXT_VI)))
            .andExpect(jsonPath("$.[*].highlight2TextEn").value(hasItem(DEFAULT_HIGHLIGHT_2_TEXT_EN)))
            .andExpect(jsonPath("$.[*].highlight3TitleVi").value(hasItem(DEFAULT_HIGHLIGHT_3_TITLE_VI)))
            .andExpect(jsonPath("$.[*].highlight3TitleEn").value(hasItem(DEFAULT_HIGHLIGHT_3_TITLE_EN)))
            .andExpect(jsonPath("$.[*].highlight3TextVi").value(hasItem(DEFAULT_HIGHLIGHT_3_TEXT_VI)))
            .andExpect(jsonPath("$.[*].highlight3TextEn").value(hasItem(DEFAULT_HIGHLIGHT_3_TEXT_EN)))
            .andExpect(jsonPath("$.[*].hotline").value(hasItem(DEFAULT_HOTLINE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].whatsappNumber").value(hasItem(DEFAULT_WHATSAPP_NUMBER)))
            .andExpect(jsonPath("$.[*].zaloNumber").value(hasItem(DEFAULT_ZALO_NUMBER)))
            .andExpect(jsonPath("$.[*].messengerUrl").value(hasItem(DEFAULT_MESSENGER_URL)))
            .andExpect(jsonPath("$.[*].facebookUrl").value(hasItem(DEFAULT_FACEBOOK_URL)))
            .andExpect(jsonPath("$.[*].instagramUrl").value(hasItem(DEFAULT_INSTAGRAM_URL)))
            .andExpect(jsonPath("$.[*].tiktokUrl").value(hasItem(DEFAULT_TIKTOK_URL)))
            .andExpect(jsonPath("$.[*].youtubeUrl").value(hasItem(DEFAULT_YOUTUBE_URL)))
            .andExpect(jsonPath("$.[*].openingHoursVi").value(hasItem(DEFAULT_OPENING_HOURS_VI)))
            .andExpect(jsonPath("$.[*].openingHoursEn").value(hasItem(DEFAULT_OPENING_HOURS_EN)))
            .andExpect(jsonPath("$.[*].metaDescriptionVi").value(hasItem(DEFAULT_META_DESCRIPTION_VI)))
            .andExpect(jsonPath("$.[*].metaDescriptionEn").value(hasItem(DEFAULT_META_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].policyContentVi").value(hasItem(DEFAULT_POLICY_CONTENT_VI)))
            .andExpect(jsonPath("$.[*].policyContentEn").value(hasItem(DEFAULT_POLICY_CONTENT_EN)));
    }

    @Test
    @Transactional
    void getSiteSetting() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        // Get the siteSetting
        restSiteSettingMockMvc
            .perform(get(ENTITY_API_URL_ID, siteSetting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(siteSetting.getId().intValue()))
            .andExpect(jsonPath("$.siteName").value(DEFAULT_SITE_NAME))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.sloganVi").value(DEFAULT_SLOGAN_VI))
            .andExpect(jsonPath("$.sloganEn").value(DEFAULT_SLOGAN_EN))
            .andExpect(jsonPath("$.heroSubtitleVi").value(DEFAULT_HERO_SUBTITLE_VI))
            .andExpect(jsonPath("$.heroSubtitleEn").value(DEFAULT_HERO_SUBTITLE_EN))
            .andExpect(jsonPath("$.heroImageUrl").value(DEFAULT_HERO_IMAGE_URL))
            .andExpect(jsonPath("$.heroVideoUrl").value(DEFAULT_HERO_VIDEO_URL))
            .andExpect(jsonPath("$.heroVideoEnabled").value(DEFAULT_HERO_VIDEO_ENABLED))
            .andExpect(jsonPath("$.backgroundMusicUrl").value(DEFAULT_BACKGROUND_MUSIC_URL))
            .andExpect(jsonPath("$.aboutTitleVi").value(DEFAULT_ABOUT_TITLE_VI))
            .andExpect(jsonPath("$.aboutTitleEn").value(DEFAULT_ABOUT_TITLE_EN))
            .andExpect(jsonPath("$.aboutContentVi").value(DEFAULT_ABOUT_CONTENT_VI))
            .andExpect(jsonPath("$.aboutContentEn").value(DEFAULT_ABOUT_CONTENT_EN))
            .andExpect(jsonPath("$.highlight1TitleVi").value(DEFAULT_HIGHLIGHT_1_TITLE_VI))
            .andExpect(jsonPath("$.highlight1TitleEn").value(DEFAULT_HIGHLIGHT_1_TITLE_EN))
            .andExpect(jsonPath("$.highlight1TextVi").value(DEFAULT_HIGHLIGHT_1_TEXT_VI))
            .andExpect(jsonPath("$.highlight1TextEn").value(DEFAULT_HIGHLIGHT_1_TEXT_EN))
            .andExpect(jsonPath("$.highlight2TitleVi").value(DEFAULT_HIGHLIGHT_2_TITLE_VI))
            .andExpect(jsonPath("$.highlight2TitleEn").value(DEFAULT_HIGHLIGHT_2_TITLE_EN))
            .andExpect(jsonPath("$.highlight2TextVi").value(DEFAULT_HIGHLIGHT_2_TEXT_VI))
            .andExpect(jsonPath("$.highlight2TextEn").value(DEFAULT_HIGHLIGHT_2_TEXT_EN))
            .andExpect(jsonPath("$.highlight3TitleVi").value(DEFAULT_HIGHLIGHT_3_TITLE_VI))
            .andExpect(jsonPath("$.highlight3TitleEn").value(DEFAULT_HIGHLIGHT_3_TITLE_EN))
            .andExpect(jsonPath("$.highlight3TextVi").value(DEFAULT_HIGHLIGHT_3_TEXT_VI))
            .andExpect(jsonPath("$.highlight3TextEn").value(DEFAULT_HIGHLIGHT_3_TEXT_EN))
            .andExpect(jsonPath("$.hotline").value(DEFAULT_HOTLINE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.whatsappNumber").value(DEFAULT_WHATSAPP_NUMBER))
            .andExpect(jsonPath("$.zaloNumber").value(DEFAULT_ZALO_NUMBER))
            .andExpect(jsonPath("$.messengerUrl").value(DEFAULT_MESSENGER_URL))
            .andExpect(jsonPath("$.facebookUrl").value(DEFAULT_FACEBOOK_URL))
            .andExpect(jsonPath("$.instagramUrl").value(DEFAULT_INSTAGRAM_URL))
            .andExpect(jsonPath("$.tiktokUrl").value(DEFAULT_TIKTOK_URL))
            .andExpect(jsonPath("$.youtubeUrl").value(DEFAULT_YOUTUBE_URL))
            .andExpect(jsonPath("$.openingHoursVi").value(DEFAULT_OPENING_HOURS_VI))
            .andExpect(jsonPath("$.openingHoursEn").value(DEFAULT_OPENING_HOURS_EN))
            .andExpect(jsonPath("$.metaDescriptionVi").value(DEFAULT_META_DESCRIPTION_VI))
            .andExpect(jsonPath("$.metaDescriptionEn").value(DEFAULT_META_DESCRIPTION_EN))
            .andExpect(jsonPath("$.policyContentVi").value(DEFAULT_POLICY_CONTENT_VI))
            .andExpect(jsonPath("$.policyContentEn").value(DEFAULT_POLICY_CONTENT_EN));
    }

    @Test
    @Transactional
    void getNonExistingSiteSetting() throws Exception {
        // Get the siteSetting
        restSiteSettingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSiteSetting() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteSetting
        SiteSetting updatedSiteSetting = siteSettingRepository.findById(siteSetting.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSiteSetting are not directly saved in db
        em.detach(updatedSiteSetting);
        updatedSiteSetting
            .siteName(UPDATED_SITE_NAME)
            .logoUrl(UPDATED_LOGO_URL)
            .sloganVi(UPDATED_SLOGAN_VI)
            .sloganEn(UPDATED_SLOGAN_EN)
            .heroSubtitleVi(UPDATED_HERO_SUBTITLE_VI)
            .heroSubtitleEn(UPDATED_HERO_SUBTITLE_EN)
            .heroImageUrl(UPDATED_HERO_IMAGE_URL)
            .heroVideoUrl(UPDATED_HERO_VIDEO_URL)
            .heroVideoEnabled(UPDATED_HERO_VIDEO_ENABLED)
            .backgroundMusicUrl(UPDATED_BACKGROUND_MUSIC_URL)
            .aboutTitleVi(UPDATED_ABOUT_TITLE_VI)
            .aboutTitleEn(UPDATED_ABOUT_TITLE_EN)
            .aboutContentVi(UPDATED_ABOUT_CONTENT_VI)
            .aboutContentEn(UPDATED_ABOUT_CONTENT_EN)
            .highlight1TitleVi(UPDATED_HIGHLIGHT_1_TITLE_VI)
            .highlight1TitleEn(UPDATED_HIGHLIGHT_1_TITLE_EN)
            .highlight1TextVi(UPDATED_HIGHLIGHT_1_TEXT_VI)
            .highlight1TextEn(UPDATED_HIGHLIGHT_1_TEXT_EN)
            .highlight2TitleVi(UPDATED_HIGHLIGHT_2_TITLE_VI)
            .highlight2TitleEn(UPDATED_HIGHLIGHT_2_TITLE_EN)
            .highlight2TextVi(UPDATED_HIGHLIGHT_2_TEXT_VI)
            .highlight2TextEn(UPDATED_HIGHLIGHT_2_TEXT_EN)
            .highlight3TitleVi(UPDATED_HIGHLIGHT_3_TITLE_VI)
            .highlight3TitleEn(UPDATED_HIGHLIGHT_3_TITLE_EN)
            .highlight3TextVi(UPDATED_HIGHLIGHT_3_TEXT_VI)
            .highlight3TextEn(UPDATED_HIGHLIGHT_3_TEXT_EN)
            .hotline(UPDATED_HOTLINE)
            .email(UPDATED_EMAIL)
            .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
            .zaloNumber(UPDATED_ZALO_NUMBER)
            .messengerUrl(UPDATED_MESSENGER_URL)
            .facebookUrl(UPDATED_FACEBOOK_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .tiktokUrl(UPDATED_TIKTOK_URL)
            .youtubeUrl(UPDATED_YOUTUBE_URL)
            .openingHoursVi(UPDATED_OPENING_HOURS_VI)
            .openingHoursEn(UPDATED_OPENING_HOURS_EN)
            .metaDescriptionVi(UPDATED_META_DESCRIPTION_VI)
            .metaDescriptionEn(UPDATED_META_DESCRIPTION_EN)
            .policyContentVi(UPDATED_POLICY_CONTENT_VI)
            .policyContentEn(UPDATED_POLICY_CONTENT_EN);
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(updatedSiteSetting);

        restSiteSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, siteSettingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteSettingDTO))
            )
            .andExpect(status().isOk());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSiteSettingToMatchAllProperties(updatedSiteSetting);
    }

    @Test
    @Transactional
    void putNonExistingSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, siteSettingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(siteSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(siteSettingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSiteSettingWithPatch() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteSetting using partial update
        SiteSetting partialUpdatedSiteSetting = new SiteSetting();
        partialUpdatedSiteSetting.setId(siteSetting.getId());

        partialUpdatedSiteSetting
            .siteName(UPDATED_SITE_NAME)
            .logoUrl(UPDATED_LOGO_URL)
            .heroSubtitleVi(UPDATED_HERO_SUBTITLE_VI)
            .heroVideoEnabled(UPDATED_HERO_VIDEO_ENABLED)
            .backgroundMusicUrl(UPDATED_BACKGROUND_MUSIC_URL)
            .highlight1TitleVi(UPDATED_HIGHLIGHT_1_TITLE_VI)
            .highlight1TextVi(UPDATED_HIGHLIGHT_1_TEXT_VI)
            .highlight2TitleEn(UPDATED_HIGHLIGHT_2_TITLE_EN)
            .highlight2TextVi(UPDATED_HIGHLIGHT_2_TEXT_VI)
            .highlight3TitleVi(UPDATED_HIGHLIGHT_3_TITLE_VI)
            .highlight3TitleEn(UPDATED_HIGHLIGHT_3_TITLE_EN)
            .hotline(UPDATED_HOTLINE)
            .email(UPDATED_EMAIL)
            .zaloNumber(UPDATED_ZALO_NUMBER)
            .messengerUrl(UPDATED_MESSENGER_URL)
            .facebookUrl(UPDATED_FACEBOOK_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .youtubeUrl(UPDATED_YOUTUBE_URL)
            .metaDescriptionEn(UPDATED_META_DESCRIPTION_EN)
            .policyContentVi(UPDATED_POLICY_CONTENT_VI)
            .policyContentEn(UPDATED_POLICY_CONTENT_EN);

        restSiteSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSiteSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSiteSetting))
            )
            .andExpect(status().isOk());

        // Validate the SiteSetting in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteSettingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSiteSetting, siteSetting),
            getPersistedSiteSetting(siteSetting)
        );
    }

    @Test
    @Transactional
    void fullUpdateSiteSettingWithPatch() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the siteSetting using partial update
        SiteSetting partialUpdatedSiteSetting = new SiteSetting();
        partialUpdatedSiteSetting.setId(siteSetting.getId());

        partialUpdatedSiteSetting
            .siteName(UPDATED_SITE_NAME)
            .logoUrl(UPDATED_LOGO_URL)
            .sloganVi(UPDATED_SLOGAN_VI)
            .sloganEn(UPDATED_SLOGAN_EN)
            .heroSubtitleVi(UPDATED_HERO_SUBTITLE_VI)
            .heroSubtitleEn(UPDATED_HERO_SUBTITLE_EN)
            .heroImageUrl(UPDATED_HERO_IMAGE_URL)
            .heroVideoUrl(UPDATED_HERO_VIDEO_URL)
            .heroVideoEnabled(UPDATED_HERO_VIDEO_ENABLED)
            .backgroundMusicUrl(UPDATED_BACKGROUND_MUSIC_URL)
            .aboutTitleVi(UPDATED_ABOUT_TITLE_VI)
            .aboutTitleEn(UPDATED_ABOUT_TITLE_EN)
            .aboutContentVi(UPDATED_ABOUT_CONTENT_VI)
            .aboutContentEn(UPDATED_ABOUT_CONTENT_EN)
            .highlight1TitleVi(UPDATED_HIGHLIGHT_1_TITLE_VI)
            .highlight1TitleEn(UPDATED_HIGHLIGHT_1_TITLE_EN)
            .highlight1TextVi(UPDATED_HIGHLIGHT_1_TEXT_VI)
            .highlight1TextEn(UPDATED_HIGHLIGHT_1_TEXT_EN)
            .highlight2TitleVi(UPDATED_HIGHLIGHT_2_TITLE_VI)
            .highlight2TitleEn(UPDATED_HIGHLIGHT_2_TITLE_EN)
            .highlight2TextVi(UPDATED_HIGHLIGHT_2_TEXT_VI)
            .highlight2TextEn(UPDATED_HIGHLIGHT_2_TEXT_EN)
            .highlight3TitleVi(UPDATED_HIGHLIGHT_3_TITLE_VI)
            .highlight3TitleEn(UPDATED_HIGHLIGHT_3_TITLE_EN)
            .highlight3TextVi(UPDATED_HIGHLIGHT_3_TEXT_VI)
            .highlight3TextEn(UPDATED_HIGHLIGHT_3_TEXT_EN)
            .hotline(UPDATED_HOTLINE)
            .email(UPDATED_EMAIL)
            .whatsappNumber(UPDATED_WHATSAPP_NUMBER)
            .zaloNumber(UPDATED_ZALO_NUMBER)
            .messengerUrl(UPDATED_MESSENGER_URL)
            .facebookUrl(UPDATED_FACEBOOK_URL)
            .instagramUrl(UPDATED_INSTAGRAM_URL)
            .tiktokUrl(UPDATED_TIKTOK_URL)
            .youtubeUrl(UPDATED_YOUTUBE_URL)
            .openingHoursVi(UPDATED_OPENING_HOURS_VI)
            .openingHoursEn(UPDATED_OPENING_HOURS_EN)
            .metaDescriptionVi(UPDATED_META_DESCRIPTION_VI)
            .metaDescriptionEn(UPDATED_META_DESCRIPTION_EN)
            .policyContentVi(UPDATED_POLICY_CONTENT_VI)
            .policyContentEn(UPDATED_POLICY_CONTENT_EN);

        restSiteSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSiteSetting.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSiteSetting))
            )
            .andExpect(status().isOk());

        // Validate the SiteSetting in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteSettingUpdatableFieldsEquals(partialUpdatedSiteSetting, getPersistedSiteSetting(partialUpdatedSiteSetting));
    }

    @Test
    @Transactional
    void patchNonExistingSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, siteSettingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(siteSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(siteSettingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSiteSetting() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        siteSetting.setId(longCount.incrementAndGet());

        // Create the SiteSetting
        SiteSettingDTO siteSettingDTO = siteSettingMapper.toDto(siteSetting);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSiteSettingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(siteSettingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SiteSetting in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSiteSetting() throws Exception {
        // Initialize the database
        insertedSiteSetting = siteSettingRepository.saveAndFlush(siteSetting);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the siteSetting
        restSiteSettingMockMvc
            .perform(delete(ENTITY_API_URL_ID, siteSetting.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return siteSettingRepository.count();
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

    protected SiteSetting getPersistedSiteSetting(SiteSetting siteSetting) {
        return siteSettingRepository.findById(siteSetting.getId()).orElseThrow();
    }

    protected void assertPersistedSiteSettingToMatchAllProperties(SiteSetting expectedSiteSetting) {
        assertSiteSettingAllPropertiesEquals(expectedSiteSetting, getPersistedSiteSetting(expectedSiteSetting));
    }

    protected void assertPersistedSiteSettingToMatchUpdatableProperties(SiteSetting expectedSiteSetting) {
        assertSiteSettingAllUpdatablePropertiesEquals(expectedSiteSetting, getPersistedSiteSetting(expectedSiteSetting));
    }
}
