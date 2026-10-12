package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Booking;
import com.mycompany.myapp.domain.BookingItem;
import com.mycompany.myapp.domain.SpaService;
import com.mycompany.myapp.domain.Staff;
import com.mycompany.myapp.service.dto.BookingDTO;
import com.mycompany.myapp.service.dto.BookingItemDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.dto.StaffDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookingItem} and its DTO {@link BookingItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingItemMapper extends EntityMapper<BookingItemDTO, BookingItem> {
    @Mapping(target = "service", source = "service", qualifiedByName = "spaServiceNameVi")
    @Mapping(target = "staff", source = "staff", qualifiedByName = "staffFullName")
    @Mapping(target = "booking", source = "booking", qualifiedByName = "bookingCode")
    BookingItemDTO toDto(BookingItem s);

    @Named("spaServiceNameVi")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nameVi", source = "nameVi")
    SpaServiceDTO toDtoSpaServiceNameVi(SpaService spaService);

    @Named("staffFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    StaffDTO toDtoStaffFullName(Staff staff);

    @Named("bookingCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    BookingDTO toDtoBookingCode(Booking booking);
}
