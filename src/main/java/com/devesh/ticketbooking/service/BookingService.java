package com.devesh.ticketbooking.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.devesh.ticketbooking.dto.BookingDTO;
import com.devesh.ticketbooking.entity.Booking;
import com.devesh.ticketbooking.entity.Event;
import com.devesh.ticketbooking.entity.Seat;
import com.devesh.ticketbooking.entity.User;
import com.devesh.ticketbooking.event.BookingConfirmedEvent;
import com.devesh.ticketbooking.event.BookingEventProducer;
import com.devesh.ticketbooking.event.EmailEventProducer;
import com.devesh.ticketbooking.event.EmailNotificationEvent;
import com.devesh.ticketbooking.exception.ResourceNotFoundException;
import com.devesh.ticketbooking.exception.SeatUnavailableException;
import com.devesh.ticketbooking.repository.BookingRepository;
import com.devesh.ticketbooking.repository.EventRepository;
import com.devesh.ticketbooking.repository.SeatRepository;
import com.devesh.ticketbooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final RedisLockService redisLockService;
    private final BookingEventProducer bookingEventProducer;
    private final EmailEventProducer emailEventProducer;

    public BookingDTO createBooking(String userEmail, Long eventId, Long seatId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));

        boolean lockAcquired = redisLockService.acquireLock(eventId, seatId, user.getId());
        if (!lockAcquired) {
            throw new SeatUnavailableException(
                    "Seat is currently being held by another user. Please try a different seat.");
        }

        try {
            boolean alreadyBooked = bookingRepository
                    .findBookedSeatIdsByEventId(eventId)
                    .contains(seatId);

            if (alreadyBooked) {
                throw new SeatUnavailableException("Seat already booked for this event");
            }

            Booking booking = Booking.builder()
                    .user(user)
                    .event(event)
                    .seat(seat)
                    .status(Booking.BookingStatus.CONFIRMED)
                    .build();

            Booking saved = bookingRepository.save(booking);

            bookingEventProducer.publishBookingConfirmed(
                    BookingConfirmedEvent.builder()
                            .bookingId(saved.getId())
                            .userId(user.getId())
                            .eventId(event.getId())
                            .seatId(seat.getId())
                            .confirmedAt(LocalDateTime.now())
                            .build()
            );
            emailEventProducer.publishEmailConfirmation(
                    EmailNotificationEvent.builder()
                            .bookingId(saved.getId())
                            .userEmail(user.getEmail())
                            .userName(user.getName())
                            .eventId(event.getId())
                            .seatId(seat.getId())
                            .build()
            );

            return BookingDTO.builder()
                    .id(saved.getId())
                    .userId(user.getId())
                    .eventId(event.getId())
                    .seatId(seat.getId())
                    .status(saved.getStatus().name())
                    .build();

        } catch (Exception e) {
            redisLockService.releaseLock(eventId, seatId);
            throw e;
        }
    }
}