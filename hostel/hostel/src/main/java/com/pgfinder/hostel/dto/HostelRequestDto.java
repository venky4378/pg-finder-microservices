package com.pgfinder.hostel.dto;

import com.pgfinder.hostel.entity.GenderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class HostelRequestDto {

    @NotBlank(message = "Hostel name is required")
    @Size(min = 3, max = 100,
            message = "Hostel name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Gender type is required")
    private GenderType genderType;

    @NotBlank(message = "Contact number is required")
    @Size(min = 10, max = 10,
            message = "Contact number must be 10 digits")
    private String contactNumber;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;

    private List<Long> amenityIds;

    @Valid
    private List<RoomDto> rooms;
}