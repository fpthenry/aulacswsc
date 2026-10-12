package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.repository.BranchRepository;
import com.mycompany.myapp.service.criteria.BranchCriteria;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.mapper.BranchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Branch} entities in the database.
 * The main input is a {@link BranchCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BranchDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BranchQueryService extends QueryService<Branch> {

    private static final Logger LOG = LoggerFactory.getLogger(BranchQueryService.class);

    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    public BranchQueryService(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }

    /**
     * Return a {@link Page} of {@link BranchDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BranchDTO> findByCriteria(BranchCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Branch> specification = createSpecification(criteria);
        return branchRepository.findAll(specification, page).map(branchMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BranchCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Branch> specification = createSpecification(criteria);
        return branchRepository.count(specification);
    }

    /**
     * Function to convert {@link BranchCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Branch> createSpecification(BranchCriteria criteria) {
        Specification<Branch> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Branch_.id),
                buildStringSpecification(criteria.getNameVi(), Branch_.nameVi),
                buildStringSpecification(criteria.getNameEn(), Branch_.nameEn),
                buildStringSpecification(criteria.getSlug(), Branch_.slug),
                buildStringSpecification(criteria.getAddressVi(), Branch_.addressVi),
                buildStringSpecification(criteria.getAddressEn(), Branch_.addressEn),
                buildStringSpecification(criteria.getPhone(), Branch_.phone),
                buildStringSpecification(criteria.getWhatsapp(), Branch_.whatsapp),
                buildStringSpecification(criteria.getZalo(), Branch_.zalo),
                buildStringSpecification(criteria.getEmail(), Branch_.email),
                buildStringSpecification(criteria.getOpenTime(), Branch_.openTime),
                buildStringSpecification(criteria.getCloseTime(), Branch_.closeTime),
                buildStringSpecification(criteria.getLastBookingTime(), Branch_.lastBookingTime),
                buildRangeSpecification(criteria.getCapacity(), Branch_.capacity),
                buildStringSpecification(criteria.getMapEmbedUrl(), Branch_.mapEmbedUrl),
                buildStringSpecification(criteria.getMapDirectionUrl(), Branch_.mapDirectionUrl),
                buildStringSpecification(criteria.getImageUrl(), Branch_.imageUrl),
                buildRangeSpecification(criteria.getDisplayOrder(), Branch_.displayOrder),
                buildSpecification(criteria.getActive(), Branch_.active)
            );
        }
        return specification;
    }
}
