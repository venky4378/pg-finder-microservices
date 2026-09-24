package com.pgfinder.booking_service.controller;

import com.pgfinder.booking_service.dto.BookingRequestDto;
import com.pgfinder.booking_service.dto.BookingResponseDto;
import com.pgfinder.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 1. Create Booking: Only USER (Resident) allowed, userId forced from header
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        if (userId == null || !"USER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only residents (USER role) can make reservations.");
        }

        // Force userId from the verified Gateway header (prevent identity spoofing)
        bookingRequestDto.setUserId(userId);
        BookingResponseDto response = bookingService.createBooking(bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking deleted successfully");
    }

    // 2. Confirm Booking: Only the hostel's OWNER
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDto> confirmBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ResponseEntity.ok(bookingService.confirmBooking(id, userId, role));
    }

    // 3. Cancel Booking: Only the hostel's OWNER
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, userId, role));
    }

    // 4. Complete Booking: Only the hostel's OWNER
    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingResponseDto> completeBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        return ResponseEntity.ok(bookingService.completeBooking(id, userId, role));
    }
}