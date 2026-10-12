package com.mycompany.myapp.service.dto.site;

/**
 * Một khung giờ 15 phút. status: AVAILABLE (còn chỗ), FULL (đã đầy), PAST (đã qua).
 */
public record TimeSlotDTO(String time, String status) {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String FULL = "FULL";
    public static final String PAST = "PAST";
}
