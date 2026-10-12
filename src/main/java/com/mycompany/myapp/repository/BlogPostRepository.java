package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BlogPost;
import com.mycompany.myapp.domain.enumeration.BlogCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BlogPost entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {
    Page<BlogPost> findAllByPublishedTrue(Pageable pageable);

    Page<BlogPost> findAllByPublishedTrueAndCategory(BlogCategory category, Pageable pageable);

    Optional<BlogPost> findOneBySlugAndPublishedTrue(String slug);

    List<BlogPost> findAllByPublishedTrue();

    boolean existsBySlug(String slug);
}
