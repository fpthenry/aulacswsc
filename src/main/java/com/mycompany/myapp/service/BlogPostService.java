package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BlogPostDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.BlogPost}.
 */
public interface BlogPostService {
    /**
     * Save a blogPost.
     *
     * @param blogPostDTO the entity to save.
     * @return the persisted entity.
     */
    BlogPostDTO save(BlogPostDTO blogPostDTO);

    /**
     * Updates a blogPost.
     *
     * @param blogPostDTO the entity to update.
     * @return the persisted entity.
     */
    BlogPostDTO update(BlogPostDTO blogPostDTO);

    /**
     * Partially updates a blogPost.
     *
     * @param blogPostDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BlogPostDTO> partialUpdate(BlogPostDTO blogPostDTO);

    /**
     * Get the "id" blogPost.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BlogPostDTO> findOne(Long id);

    /**
     * Delete the "id" blogPost.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
