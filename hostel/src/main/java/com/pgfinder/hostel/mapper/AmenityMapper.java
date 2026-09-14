package com.pgfinder.hostel.mapper;

import com.pgfinder.hostel.dto.AmenityDto;
import com.pgfinder.hostel.entity.Amenity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    Amenity toEntity(AmenityDto amenityDto);
    AmenityDto toDto(Amenity amenity);
}
