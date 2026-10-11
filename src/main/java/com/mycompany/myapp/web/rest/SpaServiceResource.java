package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.SpaServiceQueryService;
import com.mycompany.myapp.service.SpaServiceService;
import com.mycompany.myapp.service.criteria.SpaServiceCriteria;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.SpaService}.
 */
@RestController
@RequestMapping("/api/spa-services")
public class SpaServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpaServiceResource.class);

    private static final String ENTITY_NAME = "spaService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpaServiceService spaServiceService;

    private final SpaServiceRepository spaServiceRepository;

    private final SpaServiceQueryService spaServiceQueryService;

    public SpaServiceResource(
        SpaServiceService spaServiceService,
        SpaServiceRepository spaServiceRepository,
        SpaServiceQueryService spaServiceQueryService
    ) {
        this.spaServiceService = spaServiceService;
        this.spaServiceRepository = spaServiceRepository;
        this.spaServiceQueryService = spaServiceQueryService;
    }

    /**
     * {@code POST  /spa-services} : Create a new spaService.
     *
     * @param spaServiceDTO the spaServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spaServiceDTO, or with status {@code 400 (Bad Request)} if the spaService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SpaServiceDTO> createSpaService(@Valid @RequestBody SpaServiceDTO spaServiceDTO) throws URISyntaxException {
        LOG.debug("REST request to save SpaService : {}", spaServiceDTO);
        if (spaServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new spaService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        spaServiceDTO = spaServiceService.save(spaServiceDTO);
        return ResponseEntity.created(new URI("/api/spa-services/" + spaServiceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, spaServiceDTO.getId().toString()))
            .body(spaServiceDTO);
    }

    /**
     * {@code PUT  /spa-services/:id} : Updates an existing spaService.
     *
     * @param id the id of the spaServiceDTO to save.
     * @param spaServiceDTO the spaServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaServiceDTO,
     * or with status {@code 400 (Bad Request)} if the spaServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spaServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpaServiceDTO> updateSpaService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpaServiceDTO spaServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SpaService : {}, {}", id, spaServiceDTO);
        if (spaServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        spaServiceDTO = spaServiceService.update(spaServiceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, spaServiceDTO.getId().toString()))
            .body(spaServiceDTO);
    }

    /**
     * {@code PATCH  /spa-services/:id} : Partial updates given fields of an existing spaService, field will ignore if it is null
     *
     * @param id the id of the spaServiceDTO to save.
     * @param spaServiceDTO the spaServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaServiceDTO,
     * or with status {@code 400 (Bad Request)} if the spaServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the spaServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the spaServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SpaServiceDTO> partialUpdateSpaService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpaServiceDTO spaServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SpaService partially : {}, {}", id, spaServiceDTO);
        if (spaServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaServiceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SpaServiceDTO> result = spaServiceService.partialUpdate(spaServiceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, spaServiceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /spa-services} : get all the spaServices.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spaServices in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SpaServiceDTO>> getAllSpaServices(
        SpaServiceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get SpaServices by criteria: {}", criteria);

        Page<SpaServiceDTO> page = spaServiceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /spa-services/count} : count all the spaServices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSpaServices(SpaServiceCriteria criteria) {
        LOG.debug("REST request to count SpaServices by criteria: {}", criteria);
        return ResponseEntity.ok().body(spaServiceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /spa-services/:id} : get the "id" spaService.
     *
     * @param id the id of the spaServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spaServiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpaServiceDTO> getSpaService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SpaService : {}", id);
        Optional<SpaServiceDTO> spaServiceDTO = spaServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spaServiceDTO);
    }

    /**
     * {@code DELETE  /spa-services/:id} : delete the "id" spaService.
     *
     * @param id the id of the spaServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpaService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SpaService : {}", id);
        spaServiceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
