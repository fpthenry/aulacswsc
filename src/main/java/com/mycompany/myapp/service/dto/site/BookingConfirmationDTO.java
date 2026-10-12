package com.mycompany.myapp.service.dto.site;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Tóm tắt lịch hẹn trả về cho khách sau khi đặt hoặc khi tra cứu.
 */
public record BookingConfirmationDTO(
    String code,
    String status,
    Instant startTime,
    Instant endTime,
    int numberOfPeople,
    int totalDurationMinutes,
    BigDecimal totalAmount,
    String customerName,
    String phone,
    String email,
    String note,
    BranchInfo branch,
    List<ServiceLine> services
) {
    public record BranchInfo(
        Long id,
        String nameVi,
        String nameEn,
        String addressVi,
        String addressEn,
        String phone,
        String mapDirectionUrl
    ) {}

    public record ServiceLine(Long id, String nameVi, String nameEn, BigDecimal unitPrice, int quantity, Integer durationMinutes) {}
}
