package com.pgfinder.user.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private LocalDate createdAt;

}
