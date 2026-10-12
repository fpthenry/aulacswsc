package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ServiceCategory;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.service.dto.ServiceCategoryDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.dto.StaffDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SpaService} and its DTO {@link SpaServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface SpaServiceMapper extends EntityMapper<SpaServiceDTO, SpaService> {
    @Mapping(target = "category", source = "category", qualifiedByName = "serviceCategoryNameVi")
    @Mapping(target = "staffs", source = "staffs", qualifiedByName = "staffIdSet")
    SpaServiceDTO toDto(SpaService s);

    @Mapping(target = "staffs", ignore = true)
    @Mapping(target = "removeStaffs", ignore = true)
    SpaService toEntity(SpaServiceDTO spaServiceDTO);

    @Named("serviceCategoryNameVi")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nameVi", source = "nameVi")
    ServiceCategoryDTO toDtoServiceCategoryNameVi(ServiceCategory serviceCategory);

    @Named("staffId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StaffDTO toDtoStaffId(Staff staff);

    @Named("staffIdSet")
    default Set<StaffDTO> toDtoStaffIdSet(Set<Staff> staff) {
        return staff.stream().map(this::toDtoStaffId).collect(Collectors.toSet());
    }
}
