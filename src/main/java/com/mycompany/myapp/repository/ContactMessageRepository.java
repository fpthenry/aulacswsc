package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ContactMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ContactMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long>, JpaSpecificationExecutor<ContactMessage> {}
