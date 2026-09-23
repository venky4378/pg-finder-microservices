package com.pgfinder.hostel.serviceImpl;

import com.pgfinder.hostel.dto.BedDto;
import com.pgfinder.hostel.entity.Bed;
import com.pgfinder.hostel.entity.BedStatus;
import com.pgfinder.hostel.entity.Room;
import com.pgfinder.hostel.exception.BedAlreadyExistsException;
import com.pgfinder.hostel.exception.BedNotFoundException;
import com.pgfinder.hostel.exception.RoomNotFoundException;
import com.pgfinder.hostel.mapper.BedMapper;
import com.pgfinder.hostel.repository.BedRepository;
import com.pgfinder.hostel.repository.RoomRepository;
import com.pgfinder.hostel.service.BedService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BedServiceImpl implements BedService {

    private final BedRepository bedRepo;
    private final BedMapper bedMapper;
    private final RoomRepository roomRepo;

    public BedServiceImpl(BedRepository bedRepo, RoomRepository roomRepo, BedMapper bedMapper) {
        this.bedRepo = bedRepo;
        this.roomRepo = roomRepo;
        this.bedMapper = bedMapper;
    }

    @Override
    public BedDto createBed(Long roomId, BedDto bedDto) {
        if (bedRepo.existsByBedNumberAndRoomId(bedDto.getBedNumber(), roomId)) {
            throw new BedAlreadyExistsException("Bed already exists: " + bedDto.getBedNumber());
        }

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() ->
                        new RoomNotFoundException("Room not found with id: " + roomId));

        Bed bed = bedMapper.toEntity(bedDto);
        bed.setRoom(room);
        Bed savedBed = bedRepo.save(bed);
        return bedMapper.toDto(savedBed);
    }

    @Override
    public List<BedDto> getAllBeds() {

        return bedRepo.findAll()
                .stream()
                .map(bedMapper::toDto)
                .toList();
    }

    @Override
    public BedDto getBedById(Long id) {

        Bed bed = bedRepo.findById(id).orElseThrow(() ->
                new BedNotFoundException("Bed not found with id: " + id));
        return bedMapper.toDto(bed);
    }

    @Override
    public BedDto updateBed(Long id, BedDto bedDto) {
        Bed existingBed = bedRepo.findById(id)
                .orElseThrow(() -> new BedNotFoundException("Bed not found with id: " + id));

        existingBed.setBedNumber(bedDto.getBedNumber());
        existingBed.setStatus(bedDto.getStatus());
        Bed updatedBed = bedRepo.save(existingBed);
        return bedMapper.toDto(updatedBed);
    }

    @Override
    public BedDto updateBedStatus(Long id, String status) {
        Bed existingBed = bedRepo.findById(id)
                .orElseThrow(() -> new BedNotFoundException("Bed not found with id: " + id));

        try {
            existingBed.setStatus(BedStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid bed status: " + status);
        }
        Bed updatedBed = bedRepo.save(existingBed);
        return bedMapper.toDto(updatedBed);
    }

    @Override
    public void deleteBed(Long id) {
        Bed bed = bedRepo.findById(id).orElseThrow(() -> new BedNotFoundException("Bed not found with id: " + id));
        bedRepo.delete(bed);
    }

    @Override
    public BedDto getBedByHostelId(Long hostelId, Long bedId) {
        Bed bed = bedRepo.findByIdAndRoomHostelId(bedId, hostelId).orElseThrow(() ->
                new BedNotFoundException("Bed " + bedId + " not found in hostel " + hostelId));
        return bedMapper.toDto(bed);
    }
}