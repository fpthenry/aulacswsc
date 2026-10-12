package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SpaService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SpaService entity.
 */
@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Long>, JpaSpecificationExecutor<SpaService> {
    default Optional<SpaService> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SpaService> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SpaService> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select spaService from SpaService spaService left join fetch spaService.category",
        countQuery = "select count(spaService) from SpaService spaService"
    )
    Page<SpaService> findAllWithToOneRelationships(Pageable pageable);

    @Query("select spaService from SpaService spaService left join fetch spaService.category")
    List<SpaService> findAllWithToOneRelationships();

    @Query("select spaService from SpaService spaService left join fetch spaService.category where spaService.id =:id")
    Optional<SpaService> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "select spaService from SpaService spaService left join fetch spaService.category " +
        "where spaService.active = true order by spaService.displayOrder asc, spaService.id asc"
    )
    List<SpaService> findAllActiveWithCategory();

    List<SpaService> findAllByIdInAndActiveTrue(Collection<Long> ids);

    boolean existsBySlug(String slug);
}
