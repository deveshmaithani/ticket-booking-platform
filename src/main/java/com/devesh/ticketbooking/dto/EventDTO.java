package com.devesh.ticketbooking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDTO {
    private Long id;
    private Long venueId;
    private String venueName;
    private String name;
    private LocalDateTime eventTime;
    private BigDecimal basePrice;
}