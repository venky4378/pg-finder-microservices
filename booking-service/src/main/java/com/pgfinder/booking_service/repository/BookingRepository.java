package com.pgfinder.booking_service.repository;

import com.pgfinder.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    boolean existsByBedIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long bedId,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );

}
