package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ServiceCategoryRepository;
import com.mycompany.myapp.service.ServiceCategoryQueryService;
import com.mycompany.myapp.service.ServiceCategoryService;
import com.mycompany.myapp.service.criteria.ServiceCategoryCriteria;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ServiceCategory}.
 */
@RestController
@RequestMapping("/api/service-categories")
public class ServiceCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceCategoryResource.class);

    private static final String ENTITY_NAME = "serviceCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceCategoryService serviceCategoryService;

    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceCategoryQueryService serviceCategoryQueryService;

    public ServiceCategoryResource(
        ServiceCategoryService serviceCategoryService,
        ServiceCategoryRepository serviceCategoryRepository,
        ServiceCategoryQueryService serviceCategoryQueryService
    ) {
        this.serviceCategoryService = serviceCategoryService;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceCategoryQueryService = serviceCategoryQueryService;
    }

    /**
     * {@code POST  /service-categories} : Create a new serviceCategory.
     *
     * @param serviceCategoryDTO the serviceCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceCategoryDTO, or with status {@code 400 (Bad Request)} if the serviceCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ServiceCategoryDTO> createServiceCategory(@Valid @RequestBody ServiceCategoryDTO serviceCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ServiceCategory : {}", serviceCategoryDTO);
        if (serviceCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new serviceCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        serviceCategoryDTO = serviceCategoryService.save(serviceCategoryDTO);
        return ResponseEntity.created(new URI("/api/service-categories/" + serviceCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, serviceCategoryDTO.getId().toString()))
            .body(serviceCategoryDTO);
    }

    /**
     * {@code PUT  /service-categories/:id} : Updates an existing serviceCategory.
     *
     * @param id the id of the serviceCategoryDTO to save.
     * @param serviceCategoryDTO the serviceCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the serviceCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategoryDTO> updateServiceCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ServiceCategoryDTO serviceCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ServiceCategory : {}, {}", id, serviceCategoryDTO);
        if (serviceCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        serviceCategoryDTO = serviceCategoryService.update(serviceCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceCategoryDTO.getId().toString()))
            .body(serviceCategoryDTO);
    }

    /**
     * {@code PATCH  /service-categories/:id} : Partial updates given fields of an existing serviceCategory, field will ignore if it is null
     *
     * @param id the id of the serviceCategoryDTO to save.
     * @param serviceCategoryDTO the serviceCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the serviceCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serviceCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceCategoryDTO> partialUpdateServiceCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ServiceCategoryDTO serviceCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ServiceCategory partially : {}, {}", id, serviceCategoryDTO);
        if (serviceCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceCategoryDTO> result = serviceCategoryService.partialUpdate(serviceCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /service-categories} : get all the serviceCategories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceCategories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ServiceCategoryDTO>> getAllServiceCategories(
        ServiceCategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ServiceCategories by criteria: {}", criteria);

        Page<ServiceCategoryDTO> page = serviceCategoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /service-categories/count} : count all the serviceCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countServiceCategories(ServiceCategoryCriteria criteria) {
        LOG.debug("REST request to count ServiceCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(serviceCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /service-categories/:id} : get the "id" serviceCategory.
     *
     * @param id the id of the serviceCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategoryDTO> getServiceCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ServiceCategory : {}", id);
        Optional<ServiceCategoryDTO> serviceCategoryDTO = serviceCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceCategoryDTO);
    }

    /**
     * {@code DELETE  /service-categories/:id} : delete the "id" serviceCategory.
     *
     * @param id the id of the serviceCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ServiceCategory : {}", id);
        serviceCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
