package com.pgfinder.booking_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Hostel ID is required")
    private Long hostelId;

    @NotNull(message = "Bed ID is required")
    private Long bedId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;
}