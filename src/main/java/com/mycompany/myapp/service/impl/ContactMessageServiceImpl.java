package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.ContactMessage;
import com.mycompany.myapp.repository.ContactMessageRepository;
import com.mycompany.myapp.service.ContactMessageService;
import com.mycompany.myapp.service.dto.ContactMessageDTO;
import com.mycompany.myapp.service.mapper.ContactMessageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.ContactMessage}.
 */
@Service
@Transactional
public class ContactMessageServiceImpl implements ContactMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

    private final ContactMessageRepository contactMessageRepository;

    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, ContactMessageMapper contactMessageMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    @Override
    public ContactMessageDTO save(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to save ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public ContactMessageDTO update(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to update ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to partially update ContactMessage : {}", contactMessageDTO);

        return contactMessageRepository
            .findById(contactMessageDTO.getId())
            .map(existingContactMessage -> {
                contactMessageMapper.partialUpdate(existingContactMessage, contactMessageDTO);

                return existingContactMessage;
            })
            .map(contactMessageRepository::save)
            .map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ContactMessage : {}", id);
        return contactMessageRepository.findById(id).map(contactMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ContactMessage : {}", id);
        contactMessageRepository.deleteById(id);
    }
}
