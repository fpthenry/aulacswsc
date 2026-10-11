package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.repository.SpaServiceRepository;
import com.mycompany.myapp.service.SpaServiceService;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.mapper.SpaServiceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SpaService}.
 */
@Service
@Transactional
public class SpaServiceServiceImpl implements SpaServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(SpaServiceServiceImpl.class);

    private final SpaServiceRepository spaServiceRepository;

    private final SpaServiceMapper spaServiceMapper;

    public SpaServiceServiceImpl(SpaServiceRepository spaServiceRepository, SpaServiceMapper spaServiceMapper) {
        this.spaServiceRepository = spaServiceRepository;
        this.spaServiceMapper = spaServiceMapper;
    }

    @Override
    public SpaServiceDTO save(SpaServiceDTO spaServiceDTO) {
        LOG.debug("Request to save SpaService : {}", spaServiceDTO);
        SpaService spaService = spaServiceMapper.toEntity(spaServiceDTO);
        spaService = spaServiceRepository.save(spaService);
        return spaServiceMapper.toDto(spaService);
    }

    @Override
    public SpaServiceDTO update(SpaServiceDTO spaServiceDTO) {
        LOG.debug("Request to update SpaService : {}", spaServiceDTO);
        SpaService spaService = spaServiceMapper.toEntity(spaServiceDTO);
        spaService = spaServiceRepository.save(spaService);
        return spaServiceMapper.toDto(spaService);
    }

    @Override
    public Optional<SpaServiceDTO> partialUpdate(SpaServiceDTO spaServiceDTO) {
        LOG.debug("Request to partially update SpaService : {}", spaServiceDTO);

        return spaServiceRepository
            .findById(spaServiceDTO.getId())
            .map(existingSpaService -> {
                spaServiceMapper.partialUpdate(existingSpaService, spaServiceDTO);

                return existingSpaService;
            })
            .map(spaServiceRepository::save)
            .map(spaServiceMapper::toDto);
    }

    public Page<SpaServiceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return spaServiceRepository.findAllWithEagerRelationships(pageable).map(spaServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpaServiceDTO> findOne(Long id) {
        LOG.debug("Request to get SpaService : {}", id);
        return spaServiceRepository.findOneWithEagerRelationships(id).map(spaServiceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SpaService : {}", id);
        spaServiceRepository.deleteById(id);
    }
}
