package com.pgfinder.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private SharingType sharingType;

    @ManyToOne
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;

    @OneToMany(
            mappedBy = "room",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Bed> beds;
}