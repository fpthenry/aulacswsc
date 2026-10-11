package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ServiceCategory}.
 */
public interface ServiceCategoryService {
    /**
     * Save a serviceCategory.
     *
     * @param serviceCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    ServiceCategoryDTO save(ServiceCategoryDTO serviceCategoryDTO);

    /**
     * Updates a serviceCategory.
     *
     * @param serviceCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    ServiceCategoryDTO update(ServiceCategoryDTO serviceCategoryDTO);

    /**
     * Partially updates a serviceCategory.
     *
     * @param serviceCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceCategoryDTO> partialUpdate(ServiceCategoryDTO serviceCategoryDTO);

    /**
     * Get the "id" serviceCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" serviceCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
