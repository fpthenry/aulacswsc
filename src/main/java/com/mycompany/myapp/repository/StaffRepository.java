package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Staff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Staff entity.
 *
 * When extending this class, extend StaffRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface StaffRepository extends StaffRepositoryWithBagRelationships, JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    default Optional<Staff> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Staff> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Staff> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(value = "select staff from Staff staff left join fetch staff.user", countQuery = "select count(staff) from Staff staff")
    Page<Staff> findAllWithToOneRelationships(Pageable pageable);

    @Query("select staff from Staff staff left join fetch staff.user")
    List<Staff> findAllWithToOneRelationships();

    @Query("select staff from Staff staff left join fetch staff.user where staff.id =:id")
    Optional<Staff> findOneWithToOneRelationships(@Param("id") Long id);
}
