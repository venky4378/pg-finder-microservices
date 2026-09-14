package com.pgfinder.hostel.mapper;

import com.pgfinder.hostel.dto.AddressDto;
import com.pgfinder.hostel.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressDto addressDto);
    AddressDto toDto(Address address);

}
