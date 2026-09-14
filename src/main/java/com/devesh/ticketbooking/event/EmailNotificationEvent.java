package com.devesh.ticketbooking.event;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmailNotificationEvent {
    private Long bookingId;
    private String userEmail;
    private String userName;
    private Long eventId;
    private Long seatId;
}