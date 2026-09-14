package com.devesh.ticketbooking.repository;

import com.devesh.ticketbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    // Finds all seat IDs already booked (CONFIRMED or HELD) for a given event
    @Query("SELECT b.seat.id FROM Booking b WHERE b.event.id = :eventId " +
           "AND b.status IN ('CONFIRMED', 'HELD')")
    List<Long> findBookedSeatIdsByEventId(@Param("eventId") Long eventId);
}