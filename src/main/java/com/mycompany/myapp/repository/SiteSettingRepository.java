package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.SiteSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SiteSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
    Optional<SiteSetting> findFirstByOrderByIdAsc();
}
