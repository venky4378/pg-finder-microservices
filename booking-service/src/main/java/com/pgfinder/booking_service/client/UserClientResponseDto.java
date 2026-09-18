package com.pgfinder.booking_service.client;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserClientResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private LocalDate createdAt;
}
