package com.devesh.ticketbooking.event;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingConfirmedEvent {
    private Long bookingId;
    private Long userId;
    private Long eventId;
    private Long seatId;
    private LocalDateTime confirmedAt;
}