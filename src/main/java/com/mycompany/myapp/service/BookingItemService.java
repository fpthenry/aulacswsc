package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BookingItemDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.BookingItem}.
 */
public interface BookingItemService {
    /**
     * Save a bookingItem.
     *
     * @param bookingItemDTO the entity to save.
     * @return the persisted entity.
     */
    BookingItemDTO save(BookingItemDTO bookingItemDTO);

    /**
     * Updates a bookingItem.
     *
     * @param bookingItemDTO the entity to update.
     * @return the persisted entity.
     */
    BookingItemDTO update(BookingItemDTO bookingItemDTO);

    /**
     * Partially updates a bookingItem.
     *
     * @param bookingItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BookingItemDTO> partialUpdate(BookingItemDTO bookingItemDTO);

    /**
     * Get all the bookingItems.
     *
     * @return the list of entities.
     */
    List<BookingItemDTO> findAll();

    /**
     * Get all the bookingItems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BookingItemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" bookingItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BookingItemDTO> findOne(Long id);

    /**
     * Delete the "id" bookingItem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
