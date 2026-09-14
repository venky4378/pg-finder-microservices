package com.pgfinder.hostel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private String contactNumber;

    private Long ownerId;

    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(
            mappedBy = "hostel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Room> rooms;

    @ManyToMany
    @JoinTable(
            name = "hostel_amenities",
            joinColumns = @JoinColumn(name = "hostel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}