package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.Branch;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Promotion;
import com.mycompany.myapp.service.dto.BookingDTO;
import com.mycompany.myapp.service.dto.BranchDTO;
import com.mycompany.myapp.service.dto.CustomerDTO;
import com.mycompany.myapp.service.dto.PromotionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booking} and its DTO {@link BookingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
    @Mapping(target = "branch", source = "branch", qualifiedByName = "branchNameVi")
    @Mapping(target = "promotion", source = "promotion", qualifiedByName = "promotionCode")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFullName")
    BookingDTO toDto(Booking s);

    @Named("branchNameVi")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nameVi", source = "nameVi")
    BranchDTO toDtoBranchNameVi(Branch branch);

    @Named("promotionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    PromotionDTO toDtoPromotionCode(Promotion promotion);

    @Named("customerFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    CustomerDTO toDtoCustomerFullName(Customer customer);
}
