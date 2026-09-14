package com.pgfinder.hostel.serviceImpl;

import com.pgfinder.hostel.dto.RoomDto;
import com.pgfinder.hostel.entity.Hostel;
import com.pgfinder.hostel.entity.Room;
import com.pgfinder.hostel.exception.HostelNotFoundException;
import com.pgfinder.hostel.exception.RoomAlreadyExistsException;
import com.pgfinder.hostel.exception.RoomNotFoundException;
import com.pgfinder.hostel.mapper.RoomMapper;
import com.pgfinder.hostel.repository.HostelRepository;
import com.pgfinder.hostel.repository.RoomRepository;
import com.pgfinder.hostel.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepo;
    private final RoomMapper roomMapper;
    private final HostelRepository hostelRepo;


    public RoomServiceImpl(RoomRepository roomRepo, HostelRepository hostelRepo, RoomMapper roomMapper) {
        this.roomRepo = roomRepo;
        this.roomMapper = roomMapper;
        this.hostelRepo = hostelRepo;
    }

    @Override
    public RoomDto createRoom(Long hostelId, RoomDto roomDto) {
        if (roomRepo.existsByRoomNumberAndHostelId(roomDto.getRoomNumber(), hostelId)) {
            throw new RoomAlreadyExistsException(
                    "Room already exists: " + roomDto.getRoomNumber());
        }
        Hostel hostel = hostelRepo.findById(hostelId)
                .orElseThrow(() ->
                        new HostelNotFoundException(
                                "Hostel not found with id: " + hostelId));
        Room room = roomMapper.toEntity(roomDto);
        room.setHostel(hostel);
        Room savedRoom = roomRepo.save(room);
        return roomMapper.toDto(savedRoom);
    }


    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms() {
        return roomRepo.findAll().stream().map(roomMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDto getRoomById(Long id) {
        Room room = roomRepo.findById(id).orElseThrow(() -> new RoomNotFoundException("Room Not Found" + id));
        return roomMapper.toDto(room);
    }

    @Override
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room existedRoom = roomRepo.findById(id).orElseThrow(() -> new RoomNotFoundException("Room Not Found" + id));
        existedRoom.setRoomNumber(roomDto.getRoomNumber());
        existedRoom.setSharingType(roomDto.getSharingType());
        Room updatedRoom = roomRepo.save(existedRoom);
        return roomMapper.toDto(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepo.findById(id).orElseThrow(() -> new RoomNotFoundException("Room Not Found " + id));
        roomRepo.delete(room);
    }
}
