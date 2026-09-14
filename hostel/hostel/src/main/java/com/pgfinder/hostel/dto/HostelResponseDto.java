package com.pgfinder.hostel.dto;

import com.pgfinder.hostel.entity.GenderType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HostelResponseDto {

    private Long id;

    private String name;

    private String description;

    private GenderType genderType;

    private String contactNumber;

    private Long ownerId;

    private AddressDto address;

    private List<RoomDto> rooms;

    private List<AmenityDto> amenities;

    private LocalDateTime createdAt;
}