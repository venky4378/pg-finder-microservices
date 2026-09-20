package com.pgfinder.booking_service.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent implements Serializable {
    private Long bookingId;
    private Long userId;
    private Long hostelId;
    private Long bedId;
    private String status;          //CREATED,CONFIRMED,CANCELLED
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime eventTimeStamp;
}
