package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SiteSettingDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.SiteSetting}.
 */
public interface SiteSettingService {
    /**
     * Save a siteSetting.
     *
     * @param siteSettingDTO the entity to save.
     * @return the persisted entity.
     */
    SiteSettingDTO save(SiteSettingDTO siteSettingDTO);

    /**
     * Updates a siteSetting.
     *
     * @param siteSettingDTO the entity to update.
     * @return the persisted entity.
     */
    SiteSettingDTO update(SiteSettingDTO siteSettingDTO);

    /**
     * Partially updates a siteSetting.
     *
     * @param siteSettingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SiteSettingDTO> partialUpdate(SiteSettingDTO siteSettingDTO);

    /**
     * Get all the siteSettings.
     *
     * @return the list of entities.
     */
    List<SiteSettingDTO> findAll();

    /**
     * Get the "id" siteSetting.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SiteSettingDTO> findOne(Long id);

    /**
     * Delete the "id" siteSetting.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
