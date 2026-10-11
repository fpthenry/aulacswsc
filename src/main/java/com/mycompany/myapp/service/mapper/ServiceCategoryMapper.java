package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServiceCategory} and its DTO {@link ServiceCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper extends EntityMapper<ServiceCategoryDTO, ServiceCategory> {}
