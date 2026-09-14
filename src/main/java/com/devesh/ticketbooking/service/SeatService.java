package com.devesh.ticketbooking.service;

import com.devesh.ticketbooking.dto.SeatDTO;
import com.devesh.ticketbooking.entity.Seat;
import com.devesh.ticketbooking.entity.Venue;
import com.devesh.ticketbooking.repository.BookingRepository;
import com.devesh.ticketbooking.repository.SeatRepository;
import com.devesh.ticketbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;

    public SeatDTO create(SeatDTO dto) {
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found: " + dto.getVenueId()));

        Seat seat = Seat.builder()
                .venue(venue)
                .seatNumber(dto.getSeatNumber())
                .section(dto.getSection())
                .build();

        Seat saved = seatRepository.save(seat);
        return SeatDTO.builder()
                .id(saved.getId())
                .venueId(venue.getId())
                .seatNumber(saved.getSeatNumber())
                .section(saved.getSection())
                .available(true)
                .build();
    }

    // Returns all seats for a venue, marked available/unavailable for a SPECIFIC event
    public List<SeatDTO> getSeatsForEvent(Long venueId, Long eventId) {
        List<Seat> allSeats = seatRepository.findByVenueId(venueId);
        Set<Long> bookedSeatIds = Set.copyOf(bookingRepository.findBookedSeatIdsByEventId(eventId));

        return allSeats.stream()
                .map(seat -> SeatDTO.builder()
                        .id(seat.getId())
                        .venueId(venueId)
                        .seatNumber(seat.getSeatNumber())
                        .section(seat.getSection())
                        .available(!bookedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}