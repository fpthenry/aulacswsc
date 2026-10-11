package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Staff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class StaffRepositoryWithBagRelationshipsImpl implements StaffRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String STAFF_PARAMETER = "staff";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Staff> fetchBagRelationships(Optional<Staff> staff) {
        return staff.map(this::fetchServices);
    }

    @Override
    public Page<Staff> fetchBagRelationships(Page<Staff> staff) {
        return new PageImpl<>(fetchBagRelationships(staff.getContent()), staff.getPageable(), staff.getTotalElements());
    }

    @Override
    public List<Staff> fetchBagRelationships(List<Staff> staff) {
        return Optional.of(staff).map(this::fetchServices).orElse(Collections.emptyList());
    }

    Staff fetchServices(Staff result) {
        return entityManager
            .createQuery("select staff from Staff staff left join fetch staff.services where staff.id = :id", Staff.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Staff> fetchServices(List<Staff> staff) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, staff.size()).forEach(index -> order.put(staff.get(index).getId(), index));
        List<Staff> result = entityManager
            .createQuery("select staff from Staff staff left join fetch staff.services where staff in :staff", Staff.class)
            .setParameter(STAFF_PARAMETER, staff)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
