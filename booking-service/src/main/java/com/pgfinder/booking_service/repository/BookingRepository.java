package com.pgfinder.booking_service.repository;

import com.pgfinder.booking_service.entity.Booking;
import com.pgfinder.booking_service.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBedIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long bedId,
            BookingStatus status,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );
}
