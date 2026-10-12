package com.mycompany.myapp.service.dto.site;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Tra cứu lịch hẹn bằng mã + số điện thoại (gửi bằng POST để số điện thoại không nằm trong URL/log).
 */
public record BookingLookupDTO(@NotBlank @Size(max = 30) String code, @NotBlank @Size(max = 20) String phone) {}
