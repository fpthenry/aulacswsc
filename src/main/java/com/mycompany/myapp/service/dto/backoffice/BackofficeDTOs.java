package com.mycompany.myapp.service.dto.backoffice;

import com.mycompany.myapp.domain.enumeration.BookingSource;
import com.mycompany.myapp.domain.enumeration.BookingStatus;
import com.mycompany.myapp.domain.enumeration.PaymentMethod;
import com.mycompany.myapp.domain.enumeration.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO cho trang quản trị (/api/backoffice/**): tổng quan và bảng lịch hẹn.
 */
public final class BackofficeDTOs {

    private BackofficeDTOs() {}

    /** Số liệu trang Tổng quan + huy hiệu trên menu. */
    public record DashboardDTO(
        int todayBookings,
        int todayGuests,
        BigDecimal todayRevenue,
        BigDecimal monthRevenue,
        int monthCompleted,
        long pendingBookings,
        long unhandledMessages,
        long pendingReviews,
        List<BookingRowDTO> todaySchedule,
        List<DayCountDTO> next7Days,
        List<TopServiceDTO> topServices
    ) {}

    /** Huy hiệu số việc cần xử lý trên menu (gọn, tải thường xuyên). */
    public record BadgesDTO(long pendingBookings, long unhandledMessages, long pendingReviews) {}

    public record DayCountDTO(LocalDate date, int bookings, int guests) {}

    public record TopServiceDTO(Long id, String name, int count) {}

    /** Một dòng lịch hẹn trên bảng lịch hẹn. */
    public record BookingRowDTO(
        Long id,
        String code,
        Instant startTime,
        Instant endTime,
        int numberOfPeople,
        BookingStatus status,
        BookingSource source,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        String note,
        Long customerId,
        String customerName,
        String customerPhone,
        Long branchId,
        String branchName,
        List<String> services,
        List<String> staff
    ) {}

    /** Đổi trạng thái / ghi thanh toán bằng một chạm. Trường null = giữ nguyên. */
    public record StatusUpdateDTO(@NotNull BookingStatus status, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {}
}
