package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PromotionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Promotion}.
 */
public interface PromotionService {
    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    PromotionDTO save(PromotionDTO promotionDTO);

    /**
     * Updates a promotion.
     *
     * @param promotionDTO the entity to update.
     * @return the persisted entity.
     */
    PromotionDTO update(PromotionDTO promotionDTO);

    /**
     * Partially updates a promotion.
     *
     * @param promotionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO);

    /**
     * Get the "id" promotion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PromotionDTO> findOne(Long id);

    /**
     * Delete the "id" promotion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
