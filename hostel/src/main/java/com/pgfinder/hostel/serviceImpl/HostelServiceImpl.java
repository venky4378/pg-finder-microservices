package com.pgfinder.hostel.serviceImpl;

import com.pgfinder.hostel.dto.HostelRequestDto;
import com.pgfinder.hostel.dto.HostelResponseDto;
import com.pgfinder.hostel.entity.Amenity;
import com.pgfinder.hostel.entity.Hostel;
import com.pgfinder.hostel.exception.HostelNotFoundException;
import com.pgfinder.hostel.mapper.HostelMapper;
import com.pgfinder.hostel.repository.AmenityRepository;
import com.pgfinder.hostel.repository.HostelRepository;
import com.pgfinder.hostel.service.HostelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostelServiceImpl implements HostelService {

    private final HostelRepository hostelRepository;
    private final AmenityRepository amenityRepository;
    private final HostelMapper hostelMapper;

    public HostelServiceImpl(
            HostelRepository hostelRepository,
            AmenityRepository amenityRepository,
            HostelMapper hostelMapper) {

        this.hostelRepository = hostelRepository;
        this.amenityRepository = amenityRepository;
        this.hostelMapper = hostelMapper;
    }

    @Override
    public HostelResponseDto createHostel(HostelRequestDto hostelRequestDto) {

        Hostel hostel = hostelMapper.toEntity(hostelRequestDto);

        if (hostelRequestDto.getAmenityIds() != null) {

            List<Amenity> amenities = amenityRepository.findAllById(hostelRequestDto.getAmenityIds());
            hostel.setAmenities(amenities);
        }
        Hostel savedHostel = hostelRepository.save(hostel);
        return hostelMapper.toResponse(savedHostel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HostelResponseDto> getAllHostels() {

        return hostelRepository.findAll()
                .stream()
                .map(hostelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HostelResponseDto getHostelById(Long id) {

        Hostel hostel = hostelRepository.findById(id).orElseThrow(() ->
                        new HostelNotFoundException("Hostel not found with id: " + id));

        return hostelMapper.toResponse(hostel);
    }

    @Override
    public HostelResponseDto updateHostel(Long id, HostelRequestDto hostelRequestDto) {

        Hostel existingHostel = hostelRepository.findById(id)
                .orElseThrow(() -> new HostelNotFoundException("Hostel not found with id: " + id));
        existingHostel.setName(hostelRequestDto.getName());
        existingHostel.setDescription(hostelRequestDto.getDescription());
        existingHostel.setGenderType(hostelRequestDto.getGenderType());
        existingHostel.setContactNumber(hostelRequestDto.getContactNumber());
        existingHostel.setOwnerId(hostelRequestDto.getOwnerId());

        if (hostelRequestDto.getAmenityIds() != null) {

            List<Amenity> amenities = amenityRepository.findAllById(hostelRequestDto.getAmenityIds());

            existingHostel.setAmenities(amenities);
        }

        Hostel updatedHostel = hostelRepository.save(existingHostel);

        return hostelMapper.toResponse(updatedHostel);
    }

    @Override
    public void deleteHostel(Long id) {

        Hostel hostel = hostelRepository.findById(id).orElseThrow(() ->
                new HostelNotFoundException("Hostel not found with id: " + id));

        hostelRepository.delete(hostel);
    }
}