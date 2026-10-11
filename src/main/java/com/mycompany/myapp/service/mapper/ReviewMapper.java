package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Review;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.service.dto.CustomerDTO;
import com.mycompany.myapp.service.dto.ReviewDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFullName")
    @Mapping(target = "service", source = "service", qualifiedByName = "spaServiceName")
    ReviewDTO toDto(Review s);

    @Named("customerFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    CustomerDTO toDtoCustomerFullName(Customer customer);

    @Named("spaServiceName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SpaServiceDTO toDtoSpaServiceName(SpaService spaService);
}
