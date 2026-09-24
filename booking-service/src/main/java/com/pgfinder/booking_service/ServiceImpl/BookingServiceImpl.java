package com.pgfinder.booking_service.serviceImpl;

import com.pgfinder.booking_service.client.BedClientResponseDto;
import com.pgfinder.booking_service.client.HostelClient;
import com.pgfinder.booking_service.client.HostelClientResponseDto;
import com.pgfinder.booking_service.client.UserClient;
import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;
import com.pgfinder.booking_service.entity.Booking;
import com.pgfinder.booking_service.entity.BookingStatus;
import com.pgfinder.booking_service.exception.BookingNotFoundException;
import com.pgfinder.booking_service.exception.InvalidBookingException;
import com.pgfinder.booking_service.mapper.BookingMapper;
import com.pgfinder.booking_service.repository.BookingRepository;
import com.pgfinder.booking_service.service.BookingEvent;
import com.pgfinder.booking_service.service.BookingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserClient userClient;
    private final HostelClient hostelClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper, UserClient userClient, HostelClient hostelClient,KafkaTemplate kafkaTemplate) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userClient = userClient;
        this.hostelClient = hostelClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional // 👈 Guarantees atomic booking creation
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        // 1. Validate booking dates
        validateBookingDates(bookingRequestDto);
        // 2. Check user exists
        userClient.getUserById(bookingRequestDto.getUserId());
        // 3. Check hostel exists
        hostelClient.getHostelById(bookingRequestDto.getHostelId());
        // 4. Check bed exists and is available
        BedClientResponseDto bed = hostelClient.getBedByHostelId(
                bookingRequestDto.getHostelId(),
                bookingRequestDto.getBedId()
        );
        if (!"AVAILABLE".equals(bed.getStatus())) {
            throw new InvalidBookingException("Bed is not available: " + bookingRequestDto.getBedId());
        }
        // 5. Date overlap conflict check
        boolean alreadyBooked = bookingRepository
                .existsByBedIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        bookingRequestDto.getBedId(),
                        BookingStatus.CANCELLED,
                        bookingRequestDto.getCheckOutDate(),
                        bookingRequestDto.getCheckInDate()
                );
        if (alreadyBooked) {
            throw new InvalidBookingException("Bed is already booked for the selected dates");
        }
        // 6. Save booking
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        booking.setStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);
        // 7. Publish Kafka event
        sendBookingEvent(savedBooking, "PENDING");
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

    @Override
    @Transactional
    public BookingResponseDto confirmBooking(Long id, Long userId, String role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        // Strict Owner Check: Only the OWNER of this hostel can confirm (Reject ADMIN)
        validateHostelOwner(booking, userId, role);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new InvalidBookingException("Booking is already confirmed");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Cannot confirm a cancelled booking");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingException("Cannot confirm a completed booking");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        // Synchronize bed status via Feign
        hostelClient.updateBedStatus(savedBooking.getBedId(), "OCCUPIED");
        sendBookingEvent(savedBooking, "CONFIRMED");
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long id, Long userId, String role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        // Strict Owner Check: Only the OWNER of this hostel can cancel (Reject ADMIN)
        validateHostelOwner(booking, userId, role);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new InvalidBookingException("Cannot cancel an already completed booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        // Release bed
        hostelClient.updateBedStatus(savedBooking.getBedId(), "AVAILABLE");
        sendBookingEvent(savedBooking, "CANCELLED");
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto completeBooking(Long id, Long userId, String role) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        // Strict Owner Check: Only the OWNER of this hostel can complete (Reject ADMIN)
        validateHostelOwner(booking, userId, role);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingException("Only CONFIRMED bookings can be completed. Current status: " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.COMPLETED);
        Booking savedBooking = bookingRepository.save(booking);
        // Release bed back to AVAILABLE
        hostelClient.updateBedStatus(savedBooking.getBedId(), "AVAILABLE");
        return bookingMapper.toResponse(savedBooking);
    }

    private void sendBookingEvent(Booking booking, String status) {
        BookingEvent event = BookingEvent.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .hostelId(booking.getHostelId())
                .bedId(booking.getBedId())
                .status(status)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .eventTimeStamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("booking-events", String.valueOf(booking.getId()), event);
        System.out.println("📢 [KAFKA EVENT PUBLISHED] Booking #" + booking.getId() + " - Status: " + status);
    }

    // Helper: Enforce that ONLY the property OWNER can manage this booking (Reject ADMIN)
    private void validateHostelOwner(Booking booking, Long userId, String role) {
        if (userId == null || !"OWNER".equalsIgnoreCase(role)) {
            throw new InvalidBookingException("Access Denied: Only property owners can perform this action.");
        }
        HostelClientResponseDto hostel = hostelClient.getHostelById(booking.getHostelId());
        if (hostel == null || !userId.equals(hostel.getOwnerId())) {
            throw new InvalidBookingException("Access Denied: You do not own the hostel for this booking.");
        }
    }

    private void validateBookingDates(BookingRequestDto bookingRequestDto) {

        if (!bookingRequestDto.getCheckOutDate()
                .isAfter(bookingRequestDto.getCheckInDate())) {
            throw new InvalidBookingException("Check-out date must be after check-in date");
        }
    }
}