package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ContactMessageAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ContactMessage;
import com.mycompany.myapp.repository.ContactMessageRepository;
import com.mycompany.myapp.service.dto.ContactMessageDTO;
import com.mycompany.myapp.service.mapper.ContactMessageMapper;
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
 * Integration tests for the {@link ContactMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactMessageResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_HANDLED = false;
    private static final Boolean UPDATED_HANDLED = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/contact-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ContactMessageMapper contactMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactMessageMockMvc;

    private ContactMessage contactMessage;

    private ContactMessage insertedContactMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactMessage createEntity() {
        return new ContactMessage()
            .fullName(DEFAULT_FULL_NAME)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL)
            .message(DEFAULT_MESSAGE)
            .handled(DEFAULT_HANDLED)
            .createdDate(DEFAULT_CREATED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContactMessage createUpdatedEntity() {
        return new ContactMessage()
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .handled(UPDATED_HANDLED)
            .createdDate(UPDATED_CREATED_DATE);
    }

    @BeforeEach
    void initTest() {
        contactMessage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContactMessage != null) {
            contactMessageRepository.delete(insertedContactMessage);
            insertedContactMessage = null;
        }
    }

    @Test
    @Transactional
    void createContactMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);
        var returnedContactMessageDTO = om.readValue(
            restContactMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContactMessageDTO.class
        );

        // Validate the ContactMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContactMessage = contactMessageMapper.toEntity(returnedContactMessageDTO);
        assertContactMessageUpdatableFieldsEquals(returnedContactMessage, getPersistedContactMessage(returnedContactMessage));

        insertedContactMessage = returnedContactMessage;
    }

    @Test
    @Transactional
    void createContactMessageWithExistingId() throws Exception {
        // Create the ContactMessage with an existing ID
        contactMessage.setId(1L);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contactMessage.setFullName(null);

        // Create the ContactMessage, which fails.
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contactMessage.setMessage(null);

        // Create the ContactMessage, which fails.
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        restContactMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContactMessages() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].handled").value(hasItem(DEFAULT_HANDLED)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }

    @Test
    @Transactional
    void getContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get the contactMessage
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, contactMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contactMessage.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.handled").value(DEFAULT_HANDLED))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    void getContactMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        Long id = contactMessage.getId();

        defaultContactMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultContactMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultContactMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllContactMessagesByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where fullName equals to
        defaultContactMessageFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllContactMessagesByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where fullName in
        defaultContactMessageFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllContactMessagesByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where fullName is not null
        defaultContactMessageFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllContactMessagesByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where fullName contains
        defaultContactMessageFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllContactMessagesByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where fullName does not contain
        defaultContactMessageFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllContactMessagesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where phone equals to
        defaultContactMessageFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where phone in
        defaultContactMessageFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where phone is not null
        defaultContactMessageFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllContactMessagesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where phone contains
        defaultContactMessageFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where phone does not contain
        defaultContactMessageFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where email equals to
        defaultContactMessageFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactMessagesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where email in
        defaultContactMessageFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactMessagesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where email is not null
        defaultContactMessageFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllContactMessagesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where email contains
        defaultContactMessageFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactMessagesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where email does not contain
        defaultContactMessageFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllContactMessagesByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where message equals to
        defaultContactMessageFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where message in
        defaultContactMessageFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where message is not null
        defaultContactMessageFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllContactMessagesByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where message contains
        defaultContactMessageFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where message does not contain
        defaultContactMessageFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByHandledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where handled equals to
        defaultContactMessageFiltering("handled.equals=" + DEFAULT_HANDLED, "handled.equals=" + UPDATED_HANDLED);
    }

    @Test
    @Transactional
    void getAllContactMessagesByHandledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where handled in
        defaultContactMessageFiltering("handled.in=" + DEFAULT_HANDLED + "," + UPDATED_HANDLED, "handled.in=" + UPDATED_HANDLED);
    }

    @Test
    @Transactional
    void getAllContactMessagesByHandledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where handled is not null
        defaultContactMessageFiltering("handled.specified=true", "handled.specified=false");
    }

    @Test
    @Transactional
    void getAllContactMessagesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where createdDate equals to
        defaultContactMessageFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllContactMessagesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where createdDate in
        defaultContactMessageFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    @Transactional
    void getAllContactMessagesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        // Get all the contactMessageList where createdDate is not null
        defaultContactMessageFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    private void defaultContactMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultContactMessageShouldBeFound(shouldBeFound);
        defaultContactMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContactMessageShouldBeFound(String filter) throws Exception {
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].handled").value(hasItem(DEFAULT_HANDLED)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));

        // Check, that the count call also returns 1
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContactMessageShouldNotBeFound(String filter) throws Exception {
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContactMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingContactMessage() throws Exception {
        // Get the contactMessage
        restContactMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage
        ContactMessage updatedContactMessage = contactMessageRepository.findById(contactMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContactMessage are not directly saved in db
        em.detach(updatedContactMessage);
        updatedContactMessage
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .handled(UPDATED_HANDLED)
            .createdDate(UPDATED_CREATED_DATE);
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(updatedContactMessage);

        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContactMessageToMatchAllProperties(updatedContactMessage);
    }

    @Test
    @Transactional
    void putNonExistingContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactMessageWithPatch() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage using partial update
        ContactMessage partialUpdatedContactMessage = new ContactMessage();
        partialUpdatedContactMessage.setId(contactMessage.getId());

        partialUpdatedContactMessage.message(UPDATED_MESSAGE).createdDate(UPDATED_CREATED_DATE);

        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContactMessage))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContactMessage, contactMessage),
            getPersistedContactMessage(contactMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateContactMessageWithPatch() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contactMessage using partial update
        ContactMessage partialUpdatedContactMessage = new ContactMessage();
        partialUpdatedContactMessage.setId(contactMessage.getId());

        partialUpdatedContactMessage
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL)
            .message(UPDATED_MESSAGE)
            .handled(UPDATED_HANDLED)
            .createdDate(UPDATED_CREATED_DATE);

        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContactMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContactMessage))
            )
            .andExpect(status().isOk());

        // Validate the ContactMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactMessageUpdatableFieldsEquals(partialUpdatedContactMessage, getPersistedContactMessage(partialUpdatedContactMessage));
    }

    @Test
    @Transactional
    void patchNonExistingContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contactMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contactMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContactMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contactMessage.setId(longCount.incrementAndGet());

        // Create the ContactMessage
        ContactMessageDTO contactMessageDTO = contactMessageMapper.toDto(contactMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contactMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContactMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContactMessage() throws Exception {
        // Initialize the database
        insertedContactMessage = contactMessageRepository.saveAndFlush(contactMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contactMessage
        restContactMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, contactMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contactMessageRepository.count();
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

    protected ContactMessage getPersistedContactMessage(ContactMessage contactMessage) {
        return contactMessageRepository.findById(contactMessage.getId()).orElseThrow();
    }

    protected void assertPersistedContactMessageToMatchAllProperties(ContactMessage expectedContactMessage) {
        assertContactMessageAllPropertiesEquals(expectedContactMessage, getPersistedContactMessage(expectedContactMessage));
    }

    protected void assertPersistedContactMessageToMatchUpdatableProperties(ContactMessage expectedContactMessage) {
        assertContactMessageAllUpdatablePropertiesEquals(expectedContactMessage, getPersistedContactMessage(expectedContactMessage));
    }
}
