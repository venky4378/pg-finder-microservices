package com.pgfinder.booking_service.service;

import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

     BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);

     List<BookingResponseDto> getAllBookings();

     BookingResponseDto getBookingById(Long id);

     BookingResponseDto updateBooking(Long id, BookingRequestDto bookingRequestDto);

     void deleteBooking(Long id);

     // Pass requester identity for ownership validation
     BookingResponseDto confirmBooking(Long id, Long userId, String role);
     BookingResponseDto cancelBooking(Long id, Long userId, String role);
     BookingResponseDto completeBooking(Long id, Long userId, String role);
}