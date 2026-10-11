package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.StaffDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Staff}.
 */
public interface StaffService {
    /**
     * Save a staff.
     *
     * @param staffDTO the entity to save.
     * @return the persisted entity.
     */
    StaffDTO save(StaffDTO staffDTO);

    /**
     * Updates a staff.
     *
     * @param staffDTO the entity to update.
     * @return the persisted entity.
     */
    StaffDTO update(StaffDTO staffDTO);

    /**
     * Partially updates a staff.
     *
     * @param staffDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StaffDTO> partialUpdate(StaffDTO staffDTO);

    /**
     * Get all the staff with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StaffDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" staff.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StaffDTO> findOne(Long id);

    /**
     * Delete the "id" staff.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
