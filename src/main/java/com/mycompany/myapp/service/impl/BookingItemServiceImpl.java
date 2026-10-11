package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.BookingItem;
import com.mycompany.myapp.repository.BookingItemRepository;
import com.mycompany.myapp.service.BookingItemService;
import com.mycompany.myapp.service.dto.BookingItemDTO;
import com.mycompany.myapp.service.mapper.BookingItemMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BookingItem}.
 */
@Service
@Transactional
public class BookingItemServiceImpl implements BookingItemService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingItemServiceImpl.class);

    private final BookingItemRepository bookingItemRepository;

    private final BookingItemMapper bookingItemMapper;

    public BookingItemServiceImpl(BookingItemRepository bookingItemRepository, BookingItemMapper bookingItemMapper) {
        this.bookingItemRepository = bookingItemRepository;
        this.bookingItemMapper = bookingItemMapper;
    }

    @Override
    public BookingItemDTO save(BookingItemDTO bookingItemDTO) {
        LOG.debug("Request to save BookingItem : {}", bookingItemDTO);
        BookingItem bookingItem = bookingItemMapper.toEntity(bookingItemDTO);
        bookingItem = bookingItemRepository.save(bookingItem);
        return bookingItemMapper.toDto(bookingItem);
    }

    @Override
    public BookingItemDTO update(BookingItemDTO bookingItemDTO) {
        LOG.debug("Request to update BookingItem : {}", bookingItemDTO);
        BookingItem bookingItem = bookingItemMapper.toEntity(bookingItemDTO);
        bookingItem = bookingItemRepository.save(bookingItem);
        return bookingItemMapper.toDto(bookingItem);
    }

    @Override
    public Optional<BookingItemDTO> partialUpdate(BookingItemDTO bookingItemDTO) {
        LOG.debug("Request to partially update BookingItem : {}", bookingItemDTO);

        return bookingItemRepository
            .findById(bookingItemDTO.getId())
            .map(existingBookingItem -> {
                bookingItemMapper.partialUpdate(existingBookingItem, bookingItemDTO);

                return existingBookingItem;
            })
            .map(bookingItemRepository::save)
            .map(bookingItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingItemDTO> findAll() {
        LOG.debug("Request to get all BookingItems");
        return bookingItemRepository.findAll().stream().map(bookingItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<BookingItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookingItemRepository.findAllWithEagerRelationships(pageable).map(bookingItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingItemDTO> findOne(Long id) {
        LOG.debug("Request to get BookingItem : {}", id);
        return bookingItemRepository.findOneWithEagerRelationships(id).map(bookingItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BookingItem : {}", id);
        bookingItemRepository.deleteById(id);
    }
}
