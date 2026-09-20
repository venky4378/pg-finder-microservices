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

     BookingResponseDto confirmBooking(Long id);

     BookingResponseDto cancelBooking(Long id);

     BookingResponseDto completeBooking(Long id);
}