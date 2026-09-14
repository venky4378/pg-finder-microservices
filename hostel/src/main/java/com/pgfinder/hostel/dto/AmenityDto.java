package com.pgfinder.hostel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AmenityDto {

    private Long id;

    @NotBlank(message = "Amenity name is required")
    @Size(min = 2, max = 50,
            message = "Amenity name must be between 2 and 50 characters")
    private String name;
}