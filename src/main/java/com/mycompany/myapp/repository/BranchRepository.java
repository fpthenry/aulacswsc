package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Branch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Branch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {
    List<Branch> findAllByActiveTrueOrderByDisplayOrderAscIdAsc();

    Optional<Branch> findOneByIdAndActiveTrue(Long id);

    boolean existsBySlug(String slug);
}
