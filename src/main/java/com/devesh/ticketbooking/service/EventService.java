package com.devesh.ticketbooking.service;

import com.devesh.ticketbooking.dto.EventDTO;
import com.devesh.ticketbooking.entity.Event;
import com.devesh.ticketbooking.entity.Venue;
import com.devesh.ticketbooking.repository.EventRepository;
import com.devesh.ticketbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventDTO create(EventDTO dto) {
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found: " + dto.getVenueId()));

        Event event = Event.builder()
                .venue(venue)
                .name(dto.getName())
                .eventTime(dto.getEventTime())
                .basePrice(dto.getBasePrice())
                .build();

        return toDTO(eventRepository.save(event));
    }

    public List<EventDTO> getAll() {
        return eventRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        return toDTO(event);
    }

    private EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .venueId(event.getVenue().getId())
                .venueName(event.getVenue().getName())
                .name(event.getName())
                .eventTime(event.getEventTime())
                .basePrice(event.getBasePrice())
                .build();
    }
}