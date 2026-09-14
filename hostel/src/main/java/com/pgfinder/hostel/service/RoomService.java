package com.pgfinder.hostel.service;

import com.pgfinder.hostel.dto.HostelRequestDto;
import com.pgfinder.hostel.dto.HostelResponseDto;
import com.pgfinder.hostel.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createRoom(Long hostelId,RoomDto roomDto);
    List<RoomDto> getAllRooms();
    RoomDto getRoomById(Long id);
    RoomDto updateRoom(Long id, RoomDto RoomDto);
    void deleteRoom(Long id);
}
