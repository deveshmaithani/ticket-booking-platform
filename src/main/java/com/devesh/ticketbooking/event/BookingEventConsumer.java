package com.devesh.ticketbooking.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingEventConsumer {

    @KafkaListener(topics = "booking-confirmed", groupId = "ticket-booking-group")
    public void consume(BookingConfirmedEvent event) {
        // In a real system, this could update an analytics dashboard,
        // increment a "tickets sold" counter, trigger a recommendation
        // engine refresh, etc. Here we just log to prove the event pipeline works.
        log.info("📊 [Kafka Consumer] Booking confirmed event received: bookingId={}, userId={}, eventId={}, seatId={}",
                event.getBookingId(), event.getUserId(), event.getEventId(), event.getSeatId());
    }
}