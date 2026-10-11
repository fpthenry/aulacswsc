package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import com.mycompany.myapp.domain.enumeration.PaymentStatus;
import com.mycompany.myapp.repository.BookingRepository;
import com.mycompany.myapp.repository.CustomerRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.BookingQueryService;
import com.mycompany.myapp.service.BookingService;
import com.mycompany.myapp.service.criteria.BookingCriteria;
import com.mycompany.myapp.service.dto.BookingDTO;
import com.mycompany.myapp.service.dto.CustomerDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Booking}.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookingResource.class);

    private static final String ENTITY_NAME = "booking";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;

    private final BookingQueryService bookingQueryService;

    private final CustomerRepository customerRepository;

    public BookingResource(
        BookingService bookingService,
        BookingRepository bookingRepository,
        BookingQueryService bookingQueryService,
        CustomerRepository customerRepository
    ) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.bookingQueryService = bookingQueryService;
        this.customerRepository = customerRepository;
    }

    /**
     * Người dùng chỉ có ROLE_CUSTOMER (không phải ADMIN/STAFF) chỉ được thao tác trên lịch hẹn của chính mình.
     */
    private boolean isCustomerOnly() {
        return SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF);
    }

    /**
     * Id hồ sơ {@link Customer} gắn với tài khoản đang đăng nhập.
     */
    private Optional<Long> currentCustomerId() {
        return SecurityUtils.getCurrentUserLogin().flatMap(customerRepository::findOneByUserLogin).map(Customer::getId);
    }

    /**
     * {@code POST  /bookings} : Create a new booking.
     *
     * @param bookingDTO the bookingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookingDTO, or with status {@code 400 (Bad Request)} if the booking has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) throws URISyntaxException {
        LOG.debug("REST request to save Booking : {}", bookingDTO);
        if (bookingDTO.getId() != null) {
            throw new BadRequestAlertException("A new booking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (isCustomerOnly()) {
            // khách hàng luôn đặt lịch cho chính mình, trạng thái & tiền do spa xác nhận
            CustomerDTO customer = new CustomerDTO();
            customer.setId(
                currentCustomerId()
                    .orElseThrow(() ->
                        new BadRequestAlertException("Current user has no customer profile", ENTITY_NAME, "nocustomerprofile")
                    )
            );
            bookingDTO.setCustomer(customer);
            bookingDTO.setStatus(BookingStatus.PENDING);
            bookingDTO.setPaymentStatus(PaymentStatus.UNPAID);
            bookingDTO.setTotalAmount(null);
            bookingDTO.setDiscountAmount(null);
        }
        bookingDTO = bookingService.save(bookingDTO);
        return ResponseEntity.created(new URI("/api/bookings/" + bookingDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookingDTO.getId().toString()))
            .body(bookingDTO);
    }

    /**
     * {@code PUT  /bookings/:id} : Updates an existing booking.
     *
     * @param id the id of the bookingDTO to save.
     * @param bookingDTO the bookingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookingDTO bookingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Booking : {}, {}", id, bookingDTO);
        if (bookingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookingDTO = bookingService.update(bookingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookingDTO.getId().toString()))
            .body(bookingDTO);
    }

    /**
     * {@code PATCH  /bookings/:id} : Partial updates given fields of an existing booking, field will ignore if it is null
     *
     * @param id the id of the bookingDTO to save.
     * @param bookingDTO the bookingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookingDTO> partialUpdateBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookingDTO bookingDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Booking partially : {}, {}", id, bookingDTO);
        if (bookingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookingDTO> result = bookingService.partialUpdate(bookingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bookings} : get all the bookings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookingDTO>> getAllBookings(
        BookingCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Bookings by criteria: {}", criteria);
        if (isCustomerOnly()) {
            Optional<Long> customerId = currentCustomerId();
            if (customerId.isEmpty()) {
                return ResponseEntity.ok().body(List.of());
            }
            criteria.customerId().setEquals(customerId.get());
        }

        Page<BookingDTO> page = bookingQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bookings/count} : count all the bookings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookings(BookingCriteria criteria) {
        LOG.debug("REST request to count Bookings by criteria: {}", criteria);
        if (isCustomerOnly()) {
            Optional<Long> customerId = currentCustomerId();
            if (customerId.isEmpty()) {
                return ResponseEntity.ok().body(0L);
            }
            criteria.customerId().setEquals(customerId.get());
        }
        return ResponseEntity.ok().body(bookingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /bookings/:id} : get the "id" booking.
     *
     * @param id the id of the bookingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Booking : {}", id);
        Optional<BookingDTO> bookingDTO = bookingService.findOne(id);
        if (isCustomerOnly()) {
            Optional<Long> customerId = currentCustomerId();
            bookingDTO = bookingDTO.filter(
                b -> b.getCustomer() != null && customerId.isPresent() && customerId.get().equals(b.getCustomer().getId())
            );
        }
        return ResponseUtil.wrapOrNotFound(bookingDTO);
    }

    /**
     * {@code DELETE  /bookings/:id} : delete the "id" booking.
     *
     * @param id the id of the bookingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Booking : {}", id);
        bookingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
