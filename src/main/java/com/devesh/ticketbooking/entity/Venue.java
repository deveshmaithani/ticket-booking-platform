package com.devesh.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private Integer totalCapacity;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Seat> seats = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "venue")
    @Builder.Default
    private List<Event> events = new java.util.ArrayList<>();
}