package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PublicBookingService;
import com.mycompany.myapp.service.RateLimiterService;
import com.mycompany.myapp.service.dto.site.BookingConfirmationDTO;
import com.mycompany.myapp.service.dto.site.BookingLookupDTO;
import com.mycompany.myapp.service.dto.site.BookingRequestDTO;
import com.mycompany.myapp.service.dto.site.TimeSlotDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Đặt lịch cho khách vãng lai (không cần đăng nhập).
 */
@RestController
@RequestMapping("/api/public/bookings")
public class PublicBookingResource {

    private final PublicBookingService publicBookingService;
    private final RateLimiterService rateLimiterService;

    public PublicBookingResource(PublicBookingService publicBookingService, RateLimiterService rateLimiterService) {
        this.publicBookingService = publicBookingService;
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * {@code GET /api/public/bookings/availability?branchId=1&date=2026-10-20&duration=90&people=2}
     * Trả danh sách khung 15 phút với trạng thái AVAILABLE / FULL / PAST.
     */
    @GetMapping("/availability")
    public ResponseEntity<List<TimeSlotDTO>> availability(
        @RequestParam("branchId") Long branchId,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(value = "duration", defaultValue = "60") int duration,
        @RequestParam(value = "people", defaultValue = "1") int people
    ) {
        int safeDuration = Math.min(Math.max(duration, 15), 8 * 60);
        int safePeople = Math.min(Math.max(people, 1), 20);
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .body(publicBookingService.availability(branchId, date, safeDuration, safePeople));
    }

    /** Đặt lịch: honeypot + tối đa 5 lần / 10 phút / IP. */
    @PostMapping("")
    public ResponseEntity<BookingConfirmationDTO> create(@Valid @RequestBody BookingRequestDTO request, HttpServletRequest http) {
        if (StringUtils.hasText(request.website())) {
            throw new BadRequestAlertException("Invalid request", "booking", "spam");
        }
        rateLimiterService.check("booking", http.getRemoteAddr(), 5, Duration.ofMinutes(10));
        BookingConfirmationDTO result = publicBookingService.create(request);
        return ResponseEntity.created(URI.create("/api/public/bookings/" + result.code())).body(result);
    }

    /** Tra cứu bằng mã + số điện thoại: tối đa 20 lần / 10 phút / IP để chống dò mã. */
    @PostMapping("/lookup")
    public ResponseEntity<BookingConfirmationDTO> lookup(@Valid @RequestBody BookingLookupDTO request, HttpServletRequest http) {
        rateLimiterService.check("lookup", http.getRemoteAddr(), 20, Duration.ofMinutes(10));
        return ResponseUtil.wrapOrNotFound(publicBookingService.lookup(request.code(), request.phone()));
    }
}
