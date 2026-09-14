package com.pgfinder.hostel.mapper;

import com.pgfinder.hostel.dto.BedDto;
import com.pgfinder.hostel.entity.Bed;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BedMapper {
    Bed toEntity(BedDto bedDto);
    BedDto toDto(Bed bed);
}
