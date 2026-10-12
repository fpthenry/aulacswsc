package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.repository.BranchRepository;
import com.mycompany.myapp.service.BranchService;
import com.mycompany.myapp.service.SlugService;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.mapper.BranchMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Branch}.
 */
@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    private static final Logger LOG = LoggerFactory.getLogger(BranchServiceImpl.class);

    private final BranchRepository branchRepository;

    private final BranchMapper branchMapper;

    private final SlugService slugService;

    public BranchServiceImpl(BranchRepository branchRepository, BranchMapper branchMapper, SlugService slugService) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
        this.slugService = slugService;
    }

    @Override
    public BranchDTO save(BranchDTO branchDTO) {
        LOG.debug("Request to save Branch : {}", branchDTO);
        // slug luôn do server sinh: tiêu đề không dấu + ngày (không nhận slug từ form)
        branchDTO.setSlug(slugService.create(branchDTO.getNameVi(), null, 180, branchRepository::existsBySlug, "co-so"));
        Branch branch = branchMapper.toEntity(branchDTO);
        branch = branchRepository.save(branch);
        return branchMapper.toDto(branch);
    }

    @Override
    public BranchDTO update(BranchDTO branchDTO) {
        LOG.debug("Request to update Branch : {}", branchDTO);
        // giữ nguyên slug đã có để link cũ không hỏng
        branchRepository.findById(branchDTO.getId()).ifPresent(existing -> branchDTO.setSlug(existing.getSlug()));
        Branch branch = branchMapper.toEntity(branchDTO);
        branch = branchRepository.save(branch);
        return branchMapper.toDto(branch);
    }

    @Override
    public Optional<BranchDTO> partialUpdate(BranchDTO branchDTO) {
        LOG.debug("Request to partially update Branch : {}", branchDTO);
        branchDTO.setSlug(null); // slug không đổi được qua PATCH (null = giữ nguyên)

        return branchRepository
            .findById(branchDTO.getId())
            .map(existingBranch -> {
                branchMapper.partialUpdate(existingBranch, branchDTO);

                return existingBranch;
            })
            .map(branchRepository::save)
            .map(branchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchDTO> findOne(Long id) {
        LOG.debug("Request to get Branch : {}", id);
        return branchRepository.findById(id).map(branchMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Branch : {}", id);
        branchRepository.deleteById(id);
    }
}
