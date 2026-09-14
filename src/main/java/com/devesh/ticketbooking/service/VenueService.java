package com.devesh.ticketbooking.service;

import com.devesh.ticketbooking.dto.VenueDTO;
import com.devesh.ticketbooking.entity.Venue;
import com.devesh.ticketbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueDTO create(VenueDTO dto) {
        Venue venue = Venue.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .totalCapacity(dto.getTotalCapacity())
                .build();
        return toDTO(venueRepository.save(venue));
    }

    public List<VenueDTO> getAll() {
        return venueRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VenueDTO getById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found: " + id));
        return toDTO(venue);
    }

    private VenueDTO toDTO(Venue venue) {
        return VenueDTO.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .totalCapacity(venue.getTotalCapacity())
                .build();
    }
}