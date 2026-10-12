package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.enumeration.BookingSource;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import com.mycompany.myapp.service.BackofficeService;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.BadgesDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.BookingRowDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.DashboardDTO;
import com.mycompany.myapp.service.dto.backoffice.BackofficeDTOs.StatusUpdateDTO;
import com.mycompany.myapp.service.dto.site.BookingConfirmationDTO;
import com.mycompany.myapp.service.dto.site.BookingRequestDTO;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API cho trang quản trị (ROLE_ADMIN, ROLE_STAFF — cấu hình trong SecurityConfiguration).
 */
@RestController
@RequestMapping("/api/backoffice")
public class BackofficeResource {

    private final BackofficeService backofficeService;

    public BackofficeResource(BackofficeService backofficeService) {
        this.backofficeService = backofficeService;
    }

    /** Số liệu trang Tổng quan. */
    @GetMapping("/dashboard")
    public DashboardDTO dashboard() {
        return backofficeService.dashboard();
    }

    /** Số việc cần xử lý cho huy hiệu trên menu. */
    @GetMapping("/badges")
    public BadgesDTO badges() {
        return backofficeService.badges();
    }

    /** {@code GET /api/backoffice/bookings?date=2026-10-12&days=1&branchId=&status=&q=} */
    @GetMapping("/bookings")
    public List<BookingRowDTO> bookings(
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(value = "days", defaultValue = "1") int days,
        @RequestParam(value = "branchId", required = false) Long branchId,
        @RequestParam(value = "status", required = false) BookingStatus status,
        @RequestParam(value = "q", required = false) String q
    ) {
        return backofficeService.bookings(date, Math.min(days, 31), branchId, status, q);
    }

    /** Đổi trạng thái / ghi thanh toán một chạm. */
    @PatchMapping("/bookings/{id}/status")
    public BookingRowDTO updateStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusUpdateDTO update) {
        return backofficeService.updateStatus(id, update);
    }

    /** Lễ tân tạo nhanh lịch hẹn (gọi điện / khách đến trực tiếp). */
    @PostMapping("/bookings")
    public ResponseEntity<BookingConfirmationDTO> quickCreate(
        @Valid @RequestBody BookingRequestDTO request,
        @RequestParam(value = "source", required = false) BookingSource source
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(backofficeService.quickCreate(request, source));
    }
}
