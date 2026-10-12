package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BranchDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Branch}.
 */
public interface BranchService {
    /**
     * Save a branch.
     *
     * @param branchDTO the entity to save.
     * @return the persisted entity.
     */
    BranchDTO save(BranchDTO branchDTO);

    /**
     * Updates a branch.
     *
     * @param branchDTO the entity to update.
     * @return the persisted entity.
     */
    BranchDTO update(BranchDTO branchDTO);

    /**
     * Partially updates a branch.
     *
     * @param branchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BranchDTO> partialUpdate(BranchDTO branchDTO);

    /**
     * Get the "id" branch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BranchDTO> findOne(Long id);

    /**
     * Delete the "id" branch.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
