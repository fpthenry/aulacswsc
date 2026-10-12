package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ServiceCategory;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ServiceCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long>, JpaSpecificationExecutor<ServiceCategory> {
    List<ServiceCategory> findAllByActiveTrueOrderByDisplayOrderAscIdAsc();

    boolean existsBySlug(String slug);
}
