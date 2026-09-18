package com.pgfinder.booking_service.mapper;

import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;
import com.pgfinder.booking_service.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toEntity(BookingRequestDto bookingRequestDto);
    BookingResponseDto toResponse(Booking booking);

}
