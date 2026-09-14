package com.devesh.ticketbooking.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeatDTO {
    private Long id;
    private Long venueId;
    private String seatNumber;
    private String section;
    private Boolean available; // computed field, not a DB column
}