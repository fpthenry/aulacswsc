package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SiteSetting;
import com.mycompany.myapp.repository.SiteSettingRepository;
import com.mycompany.myapp.service.SiteSettingService;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import com.mycompany.myapp.service.mapper.SiteSettingMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.SiteSetting}.
 */
@Service
@Transactional
public class SiteSettingServiceImpl implements SiteSettingService {

    private static final Logger LOG = LoggerFactory.getLogger(SiteSettingServiceImpl.class);

    private final SiteSettingRepository siteSettingRepository;

    private final SiteSettingMapper siteSettingMapper;

    public SiteSettingServiceImpl(SiteSettingRepository siteSettingRepository, SiteSettingMapper siteSettingMapper) {
        this.siteSettingRepository = siteSettingRepository;
        this.siteSettingMapper = siteSettingMapper;
    }

    @Override
    public SiteSettingDTO save(SiteSettingDTO siteSettingDTO) {
        LOG.debug("Request to save SiteSetting : {}", siteSettingDTO);
        SiteSetting siteSetting = siteSettingMapper.toEntity(siteSettingDTO);
        siteSetting = siteSettingRepository.save(siteSetting);
        return siteSettingMapper.toDto(siteSetting);
    }

    @Override
    public SiteSettingDTO update(SiteSettingDTO siteSettingDTO) {
        LOG.debug("Request to update SiteSetting : {}", siteSettingDTO);
        SiteSetting siteSetting = siteSettingMapper.toEntity(siteSettingDTO);
        siteSetting = siteSettingRepository.save(siteSetting);
        return siteSettingMapper.toDto(siteSetting);
    }

    @Override
    public Optional<SiteSettingDTO> partialUpdate(SiteSettingDTO siteSettingDTO) {
        LOG.debug("Request to partially update SiteSetting : {}", siteSettingDTO);

        return siteSettingRepository
            .findById(siteSettingDTO.getId())
            .map(existingSiteSetting -> {
                siteSettingMapper.partialUpdate(existingSiteSetting, siteSettingDTO);

                return existingSiteSetting;
            })
            .map(siteSettingRepository::save)
            .map(siteSettingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteSettingDTO> findAll() {
        LOG.debug("Request to get all SiteSettings");
        return siteSettingRepository.findAll().stream().map(siteSettingMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SiteSettingDTO> findOne(Long id) {
        LOG.debug("Request to get SiteSetting : {}", id);
        return siteSettingRepository.findById(id).map(siteSettingMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SiteSetting : {}", id);
        siteSettingRepository.deleteById(id);
    }
}
