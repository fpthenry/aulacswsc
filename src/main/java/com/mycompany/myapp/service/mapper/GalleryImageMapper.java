package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.domain.GalleryImage;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.dto.GalleryImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GalleryImage} and its DTO {@link GalleryImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface GalleryImageMapper extends EntityMapper<GalleryImageDTO, GalleryImage> {
    @Mapping(target = "branch", source = "branch", qualifiedByName = "branchNameVi")
    GalleryImageDTO toDto(GalleryImage s);

    @Named("branchNameVi")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nameVi", source = "nameVi")
    BranchDTO toDtoBranchNameVi(Branch branch);
}
