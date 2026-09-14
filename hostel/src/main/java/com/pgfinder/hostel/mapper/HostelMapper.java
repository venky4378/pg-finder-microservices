package com.pgfinder.hostel.mapper;

import com.pgfinder.hostel.dto.HostelRequestDto;
import com.pgfinder.hostel.dto.HostelResponseDto;
import com.pgfinder.hostel.entity.Hostel;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface HostelMapper {

    Hostel toEntity(HostelRequestDto hostelRequestDto);
    HostelResponseDto toResponse(Hostel hostel);
}
