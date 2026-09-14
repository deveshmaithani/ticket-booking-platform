package com.devesh.ticketbooking.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long seatId;
    private String status;
}