package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BookingItemAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.BookingItem;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.repository.BookingItemRepository;
import com.mycompany.myapp.service.BookingItemService;
import com.mycompany.myapp.service.dto.BookingItemDTO;
import com.mycompany.myapp.service.mapper.BookingItemMapper;
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
 * Integration tests for the {@link BookingItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookingItemResourceIT {

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Integer DEFAULT_DURATION_MINUTES = 1;
    private static final Integer UPDATED_DURATION_MINUTES = 2;

    private static final String ENTITY_API_URL = "/api/booking-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookingItemRepository bookingItemRepository;

    @Mock
    private BookingItemRepository bookingItemRepositoryMock;

    @Autowired
    private BookingItemMapper bookingItemMapper;

    @Mock
    private BookingItemService bookingItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingItemMockMvc;

    private BookingItem bookingItem;

    private BookingItem insertedBookingItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingItem createEntity(EntityManager em) {
        BookingItem bookingItem = new BookingItem()
            .price(DEFAULT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .durationMinutes(DEFAULT_DURATION_MINUTES);
        // Add required entity
        SpaService spaService;
        if (TestUtil.findAll(em, SpaService.class).isEmpty()) {
            spaService = SpaServiceResourceIT.createEntity(em);
            em.persist(spaService);
            em.flush();
        } else {
            spaService = TestUtil.findAll(em, SpaService.class).get(0);
        }
        bookingItem.setService(spaService);
        // Add required entity
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            booking = BookingResourceIT.createEntity(em);
            em.persist(booking);
            em.flush();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        bookingItem.setBooking(booking);
        return bookingItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingItem createUpdatedEntity(EntityManager em) {
        BookingItem updatedBookingItem = new BookingItem()
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .durationMinutes(UPDATED_DURATION_MINUTES);
        // Add required entity
        SpaService spaService;
        if (TestUtil.findAll(em, SpaService.class).isEmpty()) {
            spaService = SpaServiceResourceIT.createUpdatedEntity(em);
            em.persist(spaService);
            em.flush();
        } else {
            spaService = TestUtil.findAll(em, SpaService.class).get(0);
        }
        updatedBookingItem.setService(spaService);
        // Add required entity
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            booking = BookingResourceIT.createUpdatedEntity(em);
            em.persist(booking);
            em.flush();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        updatedBookingItem.setBooking(booking);
        return updatedBookingItem;
    }

    @BeforeEach
    void initTest() {
        bookingItem = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedBookingItem != null) {
            bookingItemRepository.delete(insertedBookingItem);
            insertedBookingItem = null;
        }
    }

    @Test
    @Transactional
    void createBookingItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);
        var returnedBookingItemDTO = om.readValue(
            restBookingItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookingItemDTO.class
        );

        // Validate the BookingItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookingItem = bookingItemMapper.toEntity(returnedBookingItemDTO);
        assertBookingItemUpdatableFieldsEquals(returnedBookingItem, getPersistedBookingItem(returnedBookingItem));

        insertedBookingItem = returnedBookingItem;
    }

    @Test
    @Transactional
    void createBookingItemWithExistingId() throws Exception {
        // Create the BookingItem with an existing ID
        bookingItem.setId(1L);
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingItem.setPrice(null);

        // Create the BookingItem, which fails.
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        restBookingItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookingItem.setQuantity(null);

        // Create the BookingItem, which fails.
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        restBookingItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBookingItems() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        // Get all the bookingItemList
        restBookingItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].durationMinutes").value(hasItem(DEFAULT_DURATION_MINUTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookingItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookingItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookingItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookingItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookingItem() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        // Get the bookingItem
        restBookingItemMockMvc
            .perform(get(ENTITY_API_URL_ID, bookingItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookingItem.getId().intValue()))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.durationMinutes").value(DEFAULT_DURATION_MINUTES));
    }

    @Test
    @Transactional
    void getNonExistingBookingItem() throws Exception {
        // Get the bookingItem
        restBookingItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookingItem() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingItem
        BookingItem updatedBookingItem = bookingItemRepository.findById(bookingItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookingItem are not directly saved in db
        em.detach(updatedBookingItem);
        updatedBookingItem.price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).durationMinutes(UPDATED_DURATION_MINUTES);
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(updatedBookingItem);

        restBookingItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookingItemToMatchAllProperties(updatedBookingItem);
    }

    @Test
    @Transactional
    void putNonExistingBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookingItemWithPatch() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingItem using partial update
        BookingItem partialUpdatedBookingItem = new BookingItem();
        partialUpdatedBookingItem.setId(bookingItem.getId());

        partialUpdatedBookingItem.quantity(UPDATED_QUANTITY);

        restBookingItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingItem))
            )
            .andExpect(status().isOk());

        // Validate the BookingItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookingItem, bookingItem),
            getPersistedBookingItem(bookingItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookingItemWithPatch() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingItem using partial update
        BookingItem partialUpdatedBookingItem = new BookingItem();
        partialUpdatedBookingItem.setId(bookingItem.getId());

        partialUpdatedBookingItem.price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).durationMinutes(UPDATED_DURATION_MINUTES);

        restBookingItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingItem))
            )
            .andExpect(status().isOk());

        // Validate the BookingItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingItemUpdatableFieldsEquals(partialUpdatedBookingItem, getPersistedBookingItem(partialUpdatedBookingItem));
    }

    @Test
    @Transactional
    void patchNonExistingBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookingItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookingItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingItem.setId(longCount.incrementAndGet());

        // Create the BookingItem
        BookingItemDTO bookingItemDTO = bookingItemMapper.toDto(bookingItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookingItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookingItem() throws Exception {
        // Initialize the database
        insertedBookingItem = bookingItemRepository.saveAndFlush(bookingItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookingItem
        restBookingItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookingItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookingItemRepository.count();
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

    protected BookingItem getPersistedBookingItem(BookingItem bookingItem) {
        return bookingItemRepository.findById(bookingItem.getId()).orElseThrow();
    }

    protected void assertPersistedBookingItemToMatchAllProperties(BookingItem expectedBookingItem) {
        assertBookingItemAllPropertiesEquals(expectedBookingItem, getPersistedBookingItem(expectedBookingItem));
    }

    protected void assertPersistedBookingItemToMatchUpdatableProperties(BookingItem expectedBookingItem) {
        assertBookingItemAllUpdatablePropertiesEquals(expectedBookingItem, getPersistedBookingItem(expectedBookingItem));
    }
}
