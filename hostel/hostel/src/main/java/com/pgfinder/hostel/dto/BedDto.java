package com.pgfinder.hostel.dto;

import com.pgfinder.hostel.entity.BedStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BedDto {

    private Long id;
    @NotBlank(message = "Bed number is required")
    @Size(max = 20, message = "Bed number must not exceed 20 characters")
    private String bedNumber;

    @NotNull(message = "Bed status is required")
    private BedStatus status;
}