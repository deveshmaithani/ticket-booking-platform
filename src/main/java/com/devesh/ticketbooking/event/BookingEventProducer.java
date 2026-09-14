package com.devesh.ticketbooking.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEventProducer {

    private static final String TOPIC = "booking-confirmed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        kafkaTemplate.send(TOPIC, event.getBookingId().toString(), event);
    }
}