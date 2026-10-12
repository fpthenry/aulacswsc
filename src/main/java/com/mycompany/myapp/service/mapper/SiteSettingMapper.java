package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SiteSetting;
import com.mycompany.myapp.service.dto.SiteSettingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SiteSetting} and its DTO {@link SiteSettingDTO}.
 */
@Mapper(componentModel = "spring")
public interface SiteSettingMapper extends EntityMapper<SiteSettingDTO, SiteSetting> {}
