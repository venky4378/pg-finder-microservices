package com.pgfinder.booking_service.ServiceImpl;

import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;
import com.pgfinder.booking_service.service.BookingService;

import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingService bookingService;
    private final BookingMapper

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        return null;
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return List.of();
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        return null;
    }

    @Override
    public BookingResponseDto updateBooking(BookingRequestDto bookingRequestDto, Long id) {
        return null;
    }

    @Override
    public void deleteBooking(Long id) {

    }
}
