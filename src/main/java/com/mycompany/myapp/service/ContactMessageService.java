package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ContactMessageDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ContactMessage}.
 */
public interface ContactMessageService {
    /**
     * Save a contactMessage.
     *
     * @param contactMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ContactMessageDTO save(ContactMessageDTO contactMessageDTO);

    /**
     * Updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ContactMessageDTO update(ContactMessageDTO contactMessageDTO);

    /**
     * Partially updates a contactMessage.
     *
     * @param contactMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO);

    /**
     * Get the "id" contactMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ContactMessageDTO> findOne(Long id);

    /**
     * Delete the "id" contactMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
