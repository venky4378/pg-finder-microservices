package com.pgfinder.booking_service.serviceImpl;

import com.pgfinder.booking_service.client.BedClientResponseDto;
import com.pgfinder.booking_service.client.HostelClient;
import com.pgfinder.booking_service.client.UserClient;
import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;
import com.pgfinder.booking_service.entity.Booking;
import com.pgfinder.booking_service.entity.BookingStatus;
import com.pgfinder.booking_service.exception.BookingNotFoundException;
import com.pgfinder.booking_service.exception.InvalidBookingException;
import com.pgfinder.booking_service.mapper.BookingMapper;
import com.pgfinder.booking_service.repository.BookingRepository;
import com.pgfinder.booking_service.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserClient userClient;
    private final HostelClient hostelClient;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper, UserClient userClient, HostelClient hostelClient) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userClient = userClient;
        this.hostelClient = hostelClient;
    }

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {

        // 1. Validate booking dates
        validateBookingDates(bookingRequestDto);
        // 2. Check whether user exists
        userClient.getUserById(bookingRequestDto.getUserId());

        // 3. Check whether hostel exists
        hostelClient.getHostelById(bookingRequestDto.getHostelId());
        // 4. Check whether bed exists
        BedClientResponseDto bed =
                hostelClient.getBedByHostelId(
                        bookingRequestDto.getHostelId(),
                        bookingRequestDto.getBedId()
                );        // 5. Check whether bed is available
        if (!"AVAILABLE".equals(bed.getStatus())) {
            throw new InvalidBookingException("Bed is not available: " + bookingRequestDto.getBedId());
        }
        // 6. Check whether bed is already booked for these dates
        boolean alreadyBooked =
                bookingRepository
                        .existsByBedIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                bookingRequestDto.getBedId(),
                                bookingRequestDto.getCheckOutDate(),
                                bookingRequestDto.getCheckInDate()
                        );

        if (alreadyBooked) {
            throw new InvalidBookingException(
                    "Bed is already booked for the selected dates"
            );
        }
        // 7. Create booking
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        booking.setStatus(BookingStatus.PENDING);
        // 8. Save booking
        Booking savedBooking = bookingRepository.save(booking);
        // 9. Return response
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {

        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        return bookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponseDto updateBooking(Long id, BookingRequestDto bookingRequestDto) {

        validateBookingDates(bookingRequestDto);
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        existingBooking.setUserId(bookingRequestDto.getUserId());
        existingBooking.setHostelId(bookingRequestDto.getHostelId());
        existingBooking.setBedId(bookingRequestDto.getBedId());
        existingBooking.setCheckInDate(bookingRequestDto.getCheckInDate());
        existingBooking.setCheckOutDate(bookingRequestDto.getCheckOutDate());

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        bookingRepository.delete(booking);
    }

    private void validateBookingDates(BookingRequestDto bookingRequestDto) {

        if (!bookingRequestDto.getCheckOutDate()
                .isAfter(bookingRequestDto.getCheckInDate())) {
            throw new InvalidBookingException("Check-out date must be after check-in date");
        }
    }
}