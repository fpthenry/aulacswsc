package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.StaffRepository;
import com.mycompany.myapp.service.StaffQueryService;
import com.mycompany.myapp.service.StaffService;
import com.mycompany.myapp.service.criteria.StaffCriteria;
import com.mycompany.myapp.service.dto.StaffDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Staff}.
 */
@RestController
@RequestMapping("/api/staff")
public class StaffResource {

    private static final Logger LOG = LoggerFactory.getLogger(StaffResource.class);

    private static final String ENTITY_NAME = "staff";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StaffService staffService;

    private final StaffRepository staffRepository;

    private final StaffQueryService staffQueryService;

    public StaffResource(StaffService staffService, StaffRepository staffRepository, StaffQueryService staffQueryService) {
        this.staffService = staffService;
        this.staffRepository = staffRepository;
        this.staffQueryService = staffQueryService;
    }

    /**
     * {@code POST  /staff} : Create a new staff.
     *
     * @param staffDTO the staffDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new staffDTO, or with status {@code 400 (Bad Request)} if the staff has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StaffDTO> createStaff(@Valid @RequestBody StaffDTO staffDTO) throws URISyntaxException {
        LOG.debug("REST request to save Staff : {}", staffDTO);
        if (staffDTO.getId() != null) {
            throw new BadRequestAlertException("A new staff cannot already have an ID", ENTITY_NAME, "idexists");
        }
        staffDTO = staffService.save(staffDTO);
        return ResponseEntity.created(new URI("/api/staff/" + staffDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, staffDTO.getId().toString()))
            .body(staffDTO);
    }

    /**
     * {@code PUT  /staff/:id} : Updates an existing staff.
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StaffDTO> updateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Staff : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        staffDTO = staffService.update(staffDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, staffDTO.getId().toString()))
            .body(staffDTO);
    }

    /**
     * {@code PATCH  /staff/:id} : Partial updates given fields of an existing staff, field will ignore if it is null
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 404 (Not Found)} if the staffDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StaffDTO> partialUpdateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Staff partially : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!staffRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StaffDTO> result = staffService.partialUpdate(staffDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, staffDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /staff} : get all the staff.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of staff in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StaffDTO>> getAllStaff(
        StaffCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Staff by criteria: {}", criteria);

        Page<StaffDTO> page = staffQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /staff/count} : count all the staff.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStaff(StaffCriteria criteria) {
        LOG.debug("REST request to count Staff by criteria: {}", criteria);
        return ResponseEntity.ok().body(staffQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /staff/:id} : get the "id" staff.
     *
     * @param id the id of the staffDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the staffDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffDTO> getStaff(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Staff : {}", id);
        Optional<StaffDTO> staffDTO = staffService.findOne(id);
        return ResponseUtil.wrapOrNotFound(staffDTO);
    }

    /**
     * {@code DELETE  /staff/:id} : delete the "id" staff.
     *
     * @param id the id of the staffDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Staff : {}", id);
        staffService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
