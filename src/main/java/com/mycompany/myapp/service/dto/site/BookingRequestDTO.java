package com.mycompany.myapp.service.dto.site;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Yêu cầu đặt lịch từ website.
 *
 * @param branchId       cơ sở; có thể bỏ trống khi đặt nhanh (lấy cơ sở đầu tiên)
 * @param time           giờ bắt đầu HH:mm, bội số 15 phút
 * @param numberOfPeople số người; đặt nhanh mặc định 1
 * @param fullName       bắt buộc trừ khi đặt nhanh
 * @param quick          true nếu khách dùng chế độ "Đặt nhanh"
 * @param website        ô honeypot ẩn — người thật để trống, bot thường điền vào
 */
public record BookingRequestDTO(
    Long branchId,
    @NotEmpty @Size(max = 10) List<@NotNull Long> serviceIds,
    @NotNull LocalDate date,
    @NotNull @Pattern(regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$") String time,
    @Min(1) @Max(20) Integer numberOfPeople,
    @Size(max = 150) String fullName,
    @NotBlank @Size(max = 20) String phone,
    @Email @Size(max = 150) String email,
    @Size(max = 1000) String note,
    boolean quick,
    String website
) {}
