package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    default Optional<Booking> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Booking> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Booking> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select booking from Booking booking left join fetch booking.branch left join fetch booking.promotion left join fetch booking.customer",
        countQuery = "select count(booking) from Booking booking"
    )
    Page<Booking> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select booking from Booking booking left join fetch booking.branch left join fetch booking.promotion left join fetch booking.customer"
    )
    List<Booking> findAllWithToOneRelationships();

    @Query(
        "select booking from Booking booking left join fetch booking.branch left join fetch booking.promotion left join fetch booking.customer where booking.id =:id"
    )
    Optional<Booking> findOneWithToOneRelationships(@Param("id") Long id);

    List<Booking> findAllByBranchIdAndStartTimeBetweenAndStatusNotIn(
        Long branchId,
        Instant from,
        Instant to,
        Collection<BookingStatus> excludedStatuses
    );

    Optional<Booking> findOneByCode(String code);

    boolean existsByCode(String code);
}
