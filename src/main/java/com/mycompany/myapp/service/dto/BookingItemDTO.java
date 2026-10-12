package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BookingItem} entity.
 */
@Schema(description = "Dịch vụ trong một lịch hẹn")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingItemDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal price;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    private Integer durationMinutes;

    @NotNull
    private SpaServiceDTO service;

    private StaffDTO staff;

    @NotNull
    private BookingDTO booking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SpaServiceDTO getService() {
        return service;
    }

    public void setService(SpaServiceDTO service) {
        this.service = service;
    }

    public StaffDTO getStaff() {
        return staff;
    }

    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingItemDTO)) {
            return false;
        }

        BookingItemDTO bookingItemDTO = (BookingItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookingItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingItemDTO{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", durationMinutes=" + getDurationMinutes() +
            ", service=" + getService() +
            ", staff=" + getStaff() +
            ", booking=" + getBooking() +
            "}";
    }
}
