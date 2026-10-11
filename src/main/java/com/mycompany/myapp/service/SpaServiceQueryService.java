package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.criteria.SpaServiceCriteria;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.mapper.SpaServiceMapper;
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
 * Service for executing complex queries for {@link SpaService} entities in the database.
 * The main input is a {@link SpaServiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SpaServiceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpaServiceQueryService extends QueryService<SpaService> {

    private static final Logger LOG = LoggerFactory.getLogger(SpaServiceQueryService.class);

    private final SpaServiceRepository spaServiceRepository;

    private final SpaServiceMapper spaServiceMapper;

    public SpaServiceQueryService(SpaServiceRepository spaServiceRepository, SpaServiceMapper spaServiceMapper) {
        this.spaServiceRepository = spaServiceRepository;
        this.spaServiceMapper = spaServiceMapper;
    }

    /**
     * Return a {@link Page} of {@link SpaServiceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SpaServiceDTO> findByCriteria(SpaServiceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SpaService> specification = createSpecification(criteria);
        return spaServiceRepository.findAll(specification, page).map(spaServiceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpaServiceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SpaService> specification = createSpecification(criteria);
        return spaServiceRepository.count(specification);
    }

    /**
     * Function to convert {@link SpaServiceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SpaService> createSpecification(SpaServiceCriteria criteria) {
        Specification<SpaService> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SpaService_.id),
                buildStringSpecification(criteria.getName(), SpaService_.name),
                buildStringSpecification(criteria.getSlug(), SpaService_.slug),
                buildStringSpecification(criteria.getShortDescription(), SpaService_.shortDescription),
                buildRangeSpecification(criteria.getPrice(), SpaService_.price),
                buildRangeSpecification(criteria.getSalePrice(), SpaService_.salePrice),
                buildRangeSpecification(criteria.getDurationMinutes(), SpaService_.durationMinutes),
                buildStringSpecification(criteria.getImageUrl(), SpaService_.imageUrl),
                buildSpecification(criteria.getFeatured(), SpaService_.featured),
                buildSpecification(criteria.getActive(), SpaService_.active),
                buildSpecification(criteria.getCategoryId(), root -> root.join(SpaService_.category, JoinType.LEFT).get(ServiceCategory_.id)
                ),
                buildSpecification(criteria.getStaffsId(), root -> root.join(SpaService_.staffs, JoinType.LEFT).get(Staff_.id))
            );
        }
        return specification;
    }
}
