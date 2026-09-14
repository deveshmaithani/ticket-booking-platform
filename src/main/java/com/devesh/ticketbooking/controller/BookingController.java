package com.devesh.ticketbooking.controller;

import com.devesh.ticketbooking.dto.BookingDTO;
import com.devesh.ticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            Authentication authentication,
            @RequestParam Long eventId,
            @RequestParam Long seatId) {

        String userEmail = authentication.getName(); // comes from JWT via SecurityContext
        return ResponseEntity.ok(bookingService.createBooking(userEmail, eventId, seatId));
    }
}