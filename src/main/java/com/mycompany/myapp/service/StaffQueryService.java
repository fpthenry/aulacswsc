package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.repository.StaffRepository;
import com.mycompany.myapp.service.criteria.StaffCriteria;
import com.mycompany.myapp.service.dto.StaffDTO;
import com.mycompany.myapp.service.mapper.StaffMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Staff} entities in the database.
 * The main input is a {@link StaffCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StaffDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StaffQueryService extends QueryService<Staff> {

    private static final Logger LOG = LoggerFactory.getLogger(StaffQueryService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    public StaffQueryService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    /**
     * Return a {@link Page} of {@link StaffDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StaffDTO> findByCriteria(StaffCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.fetchBagRelationships(staffRepository.findAll(specification, page)).map(staffMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StaffCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Staff> specification = createSpecification(criteria);
        return staffRepository.count(specification);
    }

    /**
     * Function to convert {@link StaffCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Staff> createSpecification(StaffCriteria criteria) {
        Specification<Staff> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Staff_.id),
                buildStringSpecification(criteria.getFullName(), Staff_.fullName),
                buildStringSpecification(criteria.getPhone(), Staff_.phone),
                buildStringSpecification(criteria.getPosition(), Staff_.position),
                buildStringSpecification(criteria.getAvatarUrl(), Staff_.avatarUrl),
                buildSpecification(criteria.getActive(), Staff_.active),
                buildSpecification(criteria.getUserId(), root -> root.join(Staff_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getServicesId(), root -> root.join(Staff_.services, JoinType.LEFT).get(SpaService_.id))
            );
        }
        return specification;
    }
}
