package com.pgfinder.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bedNumber;

    @Enumerated(EnumType.STRING)
    private BedStatus status;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}