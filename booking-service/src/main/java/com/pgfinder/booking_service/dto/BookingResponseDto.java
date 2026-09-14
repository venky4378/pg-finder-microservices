package com.pgfinder.booking_service.dto;

import com.pgfinder.booking_service.entity.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {

    private Long id;

    private Long userId;

    private Long hostelId;

    private Long bedId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BookingStatus status;

    private LocalDateTime createdAt;
}