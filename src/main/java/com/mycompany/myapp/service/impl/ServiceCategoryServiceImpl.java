package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.repository.ServiceCategoryRepository;
import com.mycompany.myapp.service.ServiceCategoryService;
import com.mycompany.myapp.service.SlugService;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.mapper.ServiceCategoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ServiceCategory}.
 */
@Service
@Transactional
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceCategoryServiceImpl.class);

    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ServiceCategoryMapper serviceCategoryMapper;

    private final SlugService slugService;

    public ServiceCategoryServiceImpl(
        ServiceCategoryRepository serviceCategoryRepository,
        ServiceCategoryMapper serviceCategoryMapper,
        SlugService slugService
    ) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceCategoryMapper = serviceCategoryMapper;
        this.slugService = slugService;
    }

    @Override
    public ServiceCategoryDTO save(ServiceCategoryDTO serviceCategoryDTO) {
        LOG.debug("Request to save ServiceCategory : {}", serviceCategoryDTO);
        // slug luôn do server sinh: tiêu đề không dấu + ngày (không nhận slug từ form)
        serviceCategoryDTO.setSlug(
            slugService.create(serviceCategoryDTO.getNameVi(), null, 180, serviceCategoryRepository::existsBySlug, "danh-muc")
        );
        ServiceCategory serviceCategory = serviceCategoryMapper.toEntity(serviceCategoryDTO);
        serviceCategory = serviceCategoryRepository.save(serviceCategory);
        return serviceCategoryMapper.toDto(serviceCategory);
    }

    @Override
    public ServiceCategoryDTO update(ServiceCategoryDTO serviceCategoryDTO) {
        LOG.debug("Request to update ServiceCategory : {}", serviceCategoryDTO);
        // giữ nguyên slug đã có để link cũ không hỏng
        serviceCategoryRepository
            .findById(serviceCategoryDTO.getId())
            .ifPresent(existing -> serviceCategoryDTO.setSlug(existing.getSlug()));
        ServiceCategory serviceCategory = serviceCategoryMapper.toEntity(serviceCategoryDTO);
        serviceCategory = serviceCategoryRepository.save(serviceCategory);
        return serviceCategoryMapper.toDto(serviceCategory);
    }

    @Override
    public Optional<ServiceCategoryDTO> partialUpdate(ServiceCategoryDTO serviceCategoryDTO) {
        LOG.debug("Request to partially update ServiceCategory : {}", serviceCategoryDTO);
        serviceCategoryDTO.setSlug(null); // slug không đổi được qua PATCH (null = giữ nguyên)

        return serviceCategoryRepository
            .findById(serviceCategoryDTO.getId())
            .map(existingServiceCategory -> {
                serviceCategoryMapper.partialUpdate(existingServiceCategory, serviceCategoryDTO);

                return existingServiceCategory;
            })
            .map(serviceCategoryRepository::save)
            .map(serviceCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get ServiceCategory : {}", id);
        return serviceCategoryRepository.findById(id).map(serviceCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ServiceCategory : {}", id);
        serviceCategoryRepository.deleteById(id);
    }
}
