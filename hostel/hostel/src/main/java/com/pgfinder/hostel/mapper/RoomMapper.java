package com.pgfinder.hostel.mapper;

import com.pgfinder.hostel.dto.RoomDto;
import com.pgfinder.hostel.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toEntity(RoomDto roomDto);
    RoomDto toDto(Room room);

}
