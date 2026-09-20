package com.pgfinder.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BookingEvent implements Serializable {

        private Long bookingId;
        private Long userId;
        private Long hostelId;
        private Long bedId;
        private String status;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private LocalDateTime eventTimestamp;
    }

