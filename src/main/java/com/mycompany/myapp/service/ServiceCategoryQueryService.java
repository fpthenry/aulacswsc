package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.repository.ServiceCategoryRepository;
import com.mycompany.myapp.service.criteria.ServiceCategoryCriteria;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.mapper.ServiceCategoryMapper;
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
 * Service for executing complex queries for {@link ServiceCategory} entities in the database.
 * The main input is a {@link ServiceCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ServiceCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServiceCategoryQueryService extends QueryService<ServiceCategory> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceCategoryQueryService.class);

    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceCategoryMapper serviceCategoryMapper;

    public ServiceCategoryQueryService(ServiceCategoryRepository serviceCategoryRepository, ServiceCategoryMapper serviceCategoryMapper) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceCategoryMapper = serviceCategoryMapper;
    }

    /**
     * Return a {@link Page} of {@link ServiceCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceCategoryDTO> findByCriteria(ServiceCategoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ServiceCategory> specification = createSpecification(criteria);
        return serviceCategoryRepository.findAll(specification, page).map(serviceCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServiceCategoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ServiceCategory> specification = createSpecification(criteria);
        return serviceCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link ServiceCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ServiceCategory> createSpecification(ServiceCategoryCriteria criteria) {
        Specification<ServiceCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ServiceCategory_.id),
                buildStringSpecification(criteria.getNameVi(), ServiceCategory_.nameVi),
                buildStringSpecification(criteria.getNameEn(), ServiceCategory_.nameEn),
                buildStringSpecification(criteria.getSlug(), ServiceCategory_.slug),
                buildStringSpecification(criteria.getDescriptionVi(), ServiceCategory_.descriptionVi),
                buildStringSpecification(criteria.getDescriptionEn(), ServiceCategory_.descriptionEn),
                buildStringSpecification(criteria.getImageUrl(), ServiceCategory_.imageUrl),
                buildRangeSpecification(criteria.getDisplayOrder(), ServiceCategory_.displayOrder),
                buildSpecification(criteria.getActive(), ServiceCategory_.active),
                buildSpecification(criteria.getServicesId(), root -> root.join(ServiceCategory_.services, JoinType.LEFT).get(SpaService_.id)
                )
            );
        }
        return specification;
    }
}
