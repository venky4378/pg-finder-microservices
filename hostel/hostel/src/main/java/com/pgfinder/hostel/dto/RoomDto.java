package com.pgfinder.hostel.dto;

import com.pgfinder.hostel.entity.SharingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoomDto {

    private Long id;

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;

    @NotNull(message = "Sharing type is required")
    private SharingType sharingType;

    @Valid
    private List<BedDto> beds;
}