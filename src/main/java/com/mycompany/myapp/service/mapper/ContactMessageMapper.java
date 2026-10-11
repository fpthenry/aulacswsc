package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ContactMessage;
import com.mycompany.myapp.service.dto.ContactMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ContactMessage} and its DTO {@link ContactMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContactMessageMapper extends EntityMapper<ContactMessageDTO, ContactMessage> {}
