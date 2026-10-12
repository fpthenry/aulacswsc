package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.StaffAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.StaffRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.StaffService;
import com.mycompany.myapp.service.dto.StaffDTO;
import com.mycompany.myapp.service.mapper.StaffMapper;
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
 * Integration tests for the {@link StaffResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StaffResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_AVATAR_URL = "AAAAAAAAAA";
    private static final String UPDATED_AVATAR_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/staff";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private StaffRepository staffRepositoryMock;

    @Autowired
    private StaffMapper staffMapper;

    @Mock
    private StaffService staffServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStaffMockMvc;

    private Staff staff;

    private Staff insertedStaff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createEntity() {
        return new Staff()
            .fullName(DEFAULT_FULL_NAME)
            .phone(DEFAULT_PHONE)
            .position(DEFAULT_POSITION)
            .bio(DEFAULT_BIO)
            .avatarUrl(DEFAULT_AVATAR_URL)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createUpdatedEntity() {
        return new Staff()
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .position(UPDATED_POSITION)
            .bio(UPDATED_BIO)
            .avatarUrl(UPDATED_AVATAR_URL)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        staff = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStaff != null) {
            staffRepository.delete(insertedStaff);
            insertedStaff = null;
        }
    }

    @Test
    @Transactional
    void createStaff() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);
        var returnedStaffDTO = om.readValue(
            restStaffMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StaffDTO.class
        );

        // Validate the Staff in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStaff = staffMapper.toEntity(returnedStaffDTO);
        assertStaffUpdatableFieldsEquals(returnedStaff, getPersistedStaff(returnedStaff));

        insertedStaff = returnedStaff;
    }

    @Test
    @Transactional
    void createStaffWithExistingId() throws Exception {
        // Create the Staff with an existing ID
        staff.setId(1L);
        StaffDTO staffDTO = staffMapper.toDto(staff);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staff.setFullName(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        staff.setActive(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        restStaffMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffWithEagerRelationshipsIsEnabled() throws Exception {
        when(staffServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(staffServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStaffWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(staffServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStaffMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(staffRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get the staff
        restStaffMockMvc
            .perform(get(ENTITY_API_URL_ID, staff.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staff.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.avatarUrl").value(DEFAULT_AVATAR_URL))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getStaffByIdFiltering() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        Long id = staff.getId();

        defaultStaffFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStaffFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStaffFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStaffByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where fullName equals to
        defaultStaffFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where fullName in
        defaultStaffFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where fullName is not null
        defaultStaffFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where fullName contains
        defaultStaffFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where fullName does not contain
        defaultStaffFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phone equals to
        defaultStaffFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phone in
        defaultStaffFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phone is not null
        defaultStaffFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phone contains
        defaultStaffFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStaffByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where phone does not contain
        defaultStaffFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllStaffByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where position equals to
        defaultStaffFiltering("position.equals=" + DEFAULT_POSITION, "position.equals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllStaffByPositionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where position in
        defaultStaffFiltering("position.in=" + DEFAULT_POSITION + "," + UPDATED_POSITION, "position.in=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllStaffByPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where position is not null
        defaultStaffFiltering("position.specified=true", "position.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByPositionContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where position contains
        defaultStaffFiltering("position.contains=" + DEFAULT_POSITION, "position.contains=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllStaffByPositionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where position does not contain
        defaultStaffFiltering("position.doesNotContain=" + UPDATED_POSITION, "position.doesNotContain=" + DEFAULT_POSITION);
    }

    @Test
    @Transactional
    void getAllStaffByAvatarUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where avatarUrl equals to
        defaultStaffFiltering("avatarUrl.equals=" + DEFAULT_AVATAR_URL, "avatarUrl.equals=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllStaffByAvatarUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where avatarUrl in
        defaultStaffFiltering("avatarUrl.in=" + DEFAULT_AVATAR_URL + "," + UPDATED_AVATAR_URL, "avatarUrl.in=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllStaffByAvatarUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where avatarUrl is not null
        defaultStaffFiltering("avatarUrl.specified=true", "avatarUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByAvatarUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where avatarUrl contains
        defaultStaffFiltering("avatarUrl.contains=" + DEFAULT_AVATAR_URL, "avatarUrl.contains=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllStaffByAvatarUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where avatarUrl does not contain
        defaultStaffFiltering("avatarUrl.doesNotContain=" + UPDATED_AVATAR_URL, "avatarUrl.doesNotContain=" + DEFAULT_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllStaffByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where active equals to
        defaultStaffFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStaffByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where active in
        defaultStaffFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllStaffByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        // Get all the staffList where active is not null
        defaultStaffFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllStaffByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        staff.setUser(user);
        staffRepository.saveAndFlush(staff);
        Long userId = user.getId();
        // Get all the staffList where user equals to userId
        defaultStaffShouldBeFound("userId.equals=" + userId);

        // Get all the staffList where user equals to (userId + 1)
        defaultStaffShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllStaffByBranchIsEqualToSomething() throws Exception {
        Branch branch;
        if (TestUtil.findAll(em, Branch.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            branch = BranchResourceIT.createEntity();
        } else {
            branch = TestUtil.findAll(em, Branch.class).get(0);
        }
        em.persist(branch);
        em.flush();
        staff.setBranch(branch);
        staffRepository.saveAndFlush(staff);
        Long branchId = branch.getId();
        // Get all the staffList where branch equals to branchId
        defaultStaffShouldBeFound("branchId.equals=" + branchId);

        // Get all the staffList where branch equals to (branchId + 1)
        defaultStaffShouldNotBeFound("branchId.equals=" + (branchId + 1));
    }

    @Test
    @Transactional
    void getAllStaffByServicesIsEqualToSomething() throws Exception {
        SpaService services;
        if (TestUtil.findAll(em, SpaService.class).isEmpty()) {
            staffRepository.saveAndFlush(staff);
            services = SpaServiceResourceIT.createEntity(em);
        } else {
            services = TestUtil.findAll(em, SpaService.class).get(0);
        }
        em.persist(services);
        em.flush();
        staff.addServices(services);
        staffRepository.saveAndFlush(staff);
        Long servicesId = services.getId();
        // Get all the staffList where services equals to servicesId
        defaultStaffShouldBeFound("servicesId.equals=" + servicesId);

        // Get all the staffList where services equals to (servicesId + 1)
        defaultStaffShouldNotBeFound("servicesId.equals=" + (servicesId + 1));
    }

    private void defaultStaffFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStaffShouldBeFound(shouldBeFound);
        defaultStaffShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStaffShouldBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staff.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStaffShouldNotBeFound(String filter) throws Exception {
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStaffMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStaff() throws Exception {
        // Get the staff
        restStaffMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff
        Staff updatedStaff = staffRepository.findById(staff.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStaff are not directly saved in db
        em.detach(updatedStaff);
        updatedStaff
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .position(UPDATED_POSITION)
            .bio(UPDATED_BIO)
            .avatarUrl(UPDATED_AVATAR_URL)
            .active(UPDATED_ACTIVE);
        StaffDTO staffDTO = staffMapper.toDto(updatedStaff);

        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStaffToMatchAllProperties(updatedStaff);
    }

    @Test
    @Transactional
    void putNonExistingStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, staffDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff.fullName(UPDATED_FULL_NAME).phone(UPDATED_PHONE).active(UPDATED_ACTIVE);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStaff, staff), getPersistedStaff(staff));
    }

    @Test
    @Transactional
    void fullUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff
            .fullName(UPDATED_FULL_NAME)
            .phone(UPDATED_PHONE)
            .position(UPDATED_POSITION)
            .bio(UPDATED_BIO)
            .avatarUrl(UPDATED_AVATAR_URL)
            .active(UPDATED_ACTIVE);

        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStaff))
            )
            .andExpect(status().isOk());

        // Validate the Staff in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStaffUpdatableFieldsEquals(partialUpdatedStaff, getPersistedStaff(partialUpdatedStaff));
    }

    @Test
    @Transactional
    void patchNonExistingStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, staffDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(staffDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStaff() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        staff.setId(longCount.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaffMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(staffDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Staff in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStaff() throws Exception {
        // Initialize the database
        insertedStaff = staffRepository.saveAndFlush(staff);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the staff
        restStaffMockMvc
            .perform(delete(ENTITY_API_URL_ID, staff.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return staffRepository.count();
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

    protected Staff getPersistedStaff(Staff staff) {
        return staffRepository.findById(staff.getId()).orElseThrow();
    }

    protected void assertPersistedStaffToMatchAllProperties(Staff expectedStaff) {
        assertStaffAllPropertiesEquals(expectedStaff, getPersistedStaff(expectedStaff));
    }

    protected void assertPersistedStaffToMatchUpdatableProperties(Staff expectedStaff) {
        assertStaffAllUpdatablePropertiesEquals(expectedStaff, getPersistedStaff(expectedStaff));
    }
}
