package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.dto.StaffDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "services", source = "services", qualifiedByName = "spaServiceNameSet")
    StaffDTO toDto(Staff s);

    @Mapping(target = "removeServices", ignore = true)
    Staff toEntity(StaffDTO staffDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("spaServiceName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SpaServiceDTO toDtoSpaServiceName(SpaService spaService);

    @Named("spaServiceNameSet")
    default Set<SpaServiceDTO> toDtoSpaServiceNameSet(Set<SpaService> spaService) {
        return spaService.stream().map(this::toDtoSpaServiceName).collect(Collectors.toSet());
    }
}
