package com.pgfinder.hostel.service;

import com.pgfinder.hostel.dto.AmenityDto;

import java.util.List;

public interface AmenityService {

    AmenityDto createAmenity(AmenityDto amenityDto);

    List<AmenityDto> getAllAmenities();

    AmenityDto getAmenityById(Long id);

    AmenityDto updateAmenity(Long id, AmenityDto amenityDto);

    void deleteAmenity(Long id);
}