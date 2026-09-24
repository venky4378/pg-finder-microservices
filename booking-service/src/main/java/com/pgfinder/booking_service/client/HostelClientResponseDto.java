package com.pgfinder.booking_service.client;

import lombok.Data;

@Data
public class HostelClientResponseDto {

    private Long id;
    private String name;
    private Long ownerId; // 👈 Add this field

}