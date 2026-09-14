package com.devesh.ticketbooking.repository;

import com.devesh.ticketbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}