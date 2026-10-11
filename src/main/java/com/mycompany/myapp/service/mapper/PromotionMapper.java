package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Promotion;
import com.mycompany.myapp.service.dto.PromotionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {}
