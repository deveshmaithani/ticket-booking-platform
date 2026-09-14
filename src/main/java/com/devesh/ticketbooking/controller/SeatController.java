package com.devesh.ticketbooking.controller;

import com.devesh.ticketbooking.dto.SeatDTO;
import com.devesh.ticketbooking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatDTO> create(@RequestBody SeatDTO dto) {
        return ResponseEntity.ok(seatService.create(dto));
    }

    // e.g. GET /api/seats?venueId=1&eventId=3
    @GetMapping
    public ResponseEntity<List<SeatDTO>> getSeatsForEvent(
            @RequestParam Long venueId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(seatService.getSeatsForEvent(venueId, eventId));
    }
}