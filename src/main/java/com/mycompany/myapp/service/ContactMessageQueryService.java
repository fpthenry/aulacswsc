package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.ContactMessage;
import com.mycompany.myapp.repository.ContactMessageRepository;
import com.mycompany.myapp.service.criteria.ContactMessageCriteria;
import com.mycompany.myapp.service.dto.ContactMessageDTO;
import com.mycompany.myapp.service.mapper.ContactMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ContactMessage} entities in the database.
 * The main input is a {@link ContactMessageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ContactMessageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContactMessageQueryService extends QueryService<ContactMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(ContactMessageQueryService.class);

    private final ContactMessageRepository contactMessageRepository;

    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageQueryService(ContactMessageRepository contactMessageRepository, ContactMessageMapper contactMessageMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    /**
     * Return a {@link Page} of {@link ContactMessageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> findByCriteria(ContactMessageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ContactMessage> specification = createSpecification(criteria);
        return contactMessageRepository.findAll(specification, page).map(contactMessageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContactMessageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ContactMessage> specification = createSpecification(criteria);
        return contactMessageRepository.count(specification);
    }

    /**
     * Function to convert {@link ContactMessageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ContactMessage> createSpecification(ContactMessageCriteria criteria) {
        Specification<ContactMessage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ContactMessage_.id),
                buildStringSpecification(criteria.getFullName(), ContactMessage_.fullName),
                buildStringSpecification(criteria.getPhone(), ContactMessage_.phone),
                buildStringSpecification(criteria.getEmail(), ContactMessage_.email),
                buildStringSpecification(criteria.getMessage(), ContactMessage_.message),
                buildSpecification(criteria.getHandled(), ContactMessage_.handled),
                buildRangeSpecification(criteria.getCreatedDate(), ContactMessage_.createdDate)
            );
        }
        return specification;
    }
}
