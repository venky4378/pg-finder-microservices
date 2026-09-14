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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressLine;

    private String area;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    @OneToOne(mappedBy = "address")
    private Hostel hostel;
}