package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SpaServiceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.SpaService}.
 */
public interface SpaServiceService {
    /**
     * Save a spaService.
     *
     * @param spaServiceDTO the entity to save.
     * @return the persisted entity.
     */
    SpaServiceDTO save(SpaServiceDTO spaServiceDTO);

    /**
     * Updates a spaService.
     *
     * @param spaServiceDTO the entity to update.
     * @return the persisted entity.
     */
    SpaServiceDTO update(SpaServiceDTO spaServiceDTO);

    /**
     * Partially updates a spaService.
     *
     * @param spaServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SpaServiceDTO> partialUpdate(SpaServiceDTO spaServiceDTO);

    /**
     * Get all the spaServices with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SpaServiceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" spaService.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SpaServiceDTO> findOne(Long id);

    /**
     * Delete the "id" spaService.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
