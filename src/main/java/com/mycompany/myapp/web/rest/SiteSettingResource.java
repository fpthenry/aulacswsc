package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SiteSettingRepository;
import com.mycompany.myapp.service.SiteSettingService;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.SiteSetting}.
 */
@RestController
@RequestMapping("/api/site-settings")
public class SiteSettingResource {

    private static final Logger LOG = LoggerFactory.getLogger(SiteSettingResource.class);

    private static final String ENTITY_NAME = "siteSetting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SiteSettingService siteSettingService;

    private final SiteSettingRepository siteSettingRepository;

    public SiteSettingResource(SiteSettingService siteSettingService, SiteSettingRepository siteSettingRepository) {
        this.siteSettingService = siteSettingService;
        this.siteSettingRepository = siteSettingRepository;
    }

    /**
     * {@code POST  /site-settings} : Create a new siteSetting.
     *
     * @param siteSettingDTO the siteSettingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new siteSettingDTO, or with status {@code 400 (Bad Request)} if the siteSetting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SiteSettingDTO> createSiteSetting(@Valid @RequestBody SiteSettingDTO siteSettingDTO) throws URISyntaxException {
        LOG.debug("REST request to save SiteSetting : {}", siteSettingDTO);
        if (siteSettingDTO.getId() != null) {
            throw new BadRequestAlertException("A new siteSetting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        siteSettingDTO = siteSettingService.save(siteSettingDTO);
        return ResponseEntity.created(new URI("/api/site-settings/" + siteSettingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, siteSettingDTO.getId().toString()))
            .body(siteSettingDTO);
    }

    /**
     * {@code PUT  /site-settings/:id} : Updates an existing siteSetting.
     *
     * @param id the id of the siteSettingDTO to save.
     * @param siteSettingDTO the siteSettingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteSettingDTO,
     * or with status {@code 400 (Bad Request)} if the siteSettingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the siteSettingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SiteSettingDTO> updateSiteSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SiteSettingDTO siteSettingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SiteSetting : {}, {}", id, siteSettingDTO);
        if (siteSettingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteSettingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!siteSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        siteSettingDTO = siteSettingService.update(siteSettingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteSettingDTO.getId().toString()))
            .body(siteSettingDTO);
    }

    /**
     * {@code PATCH  /site-settings/:id} : Partial updates given fields of an existing siteSetting, field will ignore if it is null
     *
     * @param id the id of the siteSettingDTO to save.
     * @param siteSettingDTO the siteSettingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteSettingDTO,
     * or with status {@code 400 (Bad Request)} if the siteSettingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the siteSettingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the siteSettingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SiteSettingDTO> partialUpdateSiteSetting(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SiteSettingDTO siteSettingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SiteSetting partially : {}, {}", id, siteSettingDTO);
        if (siteSettingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, siteSettingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!siteSettingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SiteSettingDTO> result = siteSettingService.partialUpdate(siteSettingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteSettingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /site-settings} : get all the siteSettings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of siteSettings in body.
     */
    @GetMapping("")
    public List<SiteSettingDTO> getAllSiteSettings() {
        LOG.debug("REST request to get all SiteSettings");
        return siteSettingService.findAll();
    }

    /**
     * {@code GET  /site-settings/:id} : get the "id" siteSetting.
     *
     * @param id the id of the siteSettingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the siteSettingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SiteSettingDTO> getSiteSetting(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SiteSetting : {}", id);
        Optional<SiteSettingDTO> siteSettingDTO = siteSettingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(siteSettingDTO);
    }

    /**
     * {@code DELETE  /site-settings/:id} : delete the "id" siteSetting.
     *
     * @param id the id of the siteSettingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSiteSetting(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SiteSetting : {}", id);
        siteSettingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
