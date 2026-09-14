package com.devesh.ticketbooking.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VenueDTO {
    private Long id;
    private String name;
    private String address;
    private Integer totalCapacity;
}