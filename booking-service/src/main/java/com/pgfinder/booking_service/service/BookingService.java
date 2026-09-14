package com.pgfinder.booking_service.service;

import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

     public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);

     public List<BookingResponseDto> getAllBookings();
     public BookingResponseDto getBookingById(Long id);
     public BookingResponseDto updateBooking(BookingRequestDto bookingRequestDto,Long id);
     public void deleteBooking(Long id);


}
