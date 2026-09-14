package com.pgfinder.hostel.serviceImpl;

import com.pgfinder.hostel.dto.AmenityDto;
import com.pgfinder.hostel.entity.Amenity;
import com.pgfinder.hostel.exception.AmenityNotFoundException;
import com.pgfinder.hostel.mapper.AmenityMapper;
import com.pgfinder.hostel.repository.AmenityRepository;
import com.pgfinder.hostel.service.AmenityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public AmenityServiceImpl(
            AmenityRepository amenityRepository,
            AmenityMapper amenityMapper) {

        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    @Override
    public AmenityDto createAmenity(AmenityDto amenityDto) {

        Amenity amenity = amenityMapper.toEntity(amenityDto);

        Amenity savedAmenity = amenityRepository.save(amenity);

        return amenityMapper.toDto(savedAmenity);
    }

    @Override
    public List<AmenityDto> getAllAmenities() {

        return amenityRepository.findAll()
                .stream()
                .map(amenityMapper::toDto)
                .toList();
    }

    @Override
    public AmenityDto getAmenityById(Long id) {

        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() ->
                        new AmenityNotFoundException(
                                "Amenity not found with id: " + id));

        return amenityMapper.toDto(amenity);
    }

    @Override
    public AmenityDto updateAmenity(
            Long id,
            AmenityDto amenityDto) {

        Amenity existingAmenity = amenityRepository.findById(id)
                .orElseThrow(() ->
                        new AmenityNotFoundException(
                                "Amenity not found with id: " + id));

        existingAmenity.setName(amenityDto.getName());

        Amenity updatedAmenity =
                amenityRepository.save(existingAmenity);

        return amenityMapper.toDto(updatedAmenity);
    }

    @Override
    public void deleteAmenity(Long id) {

        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() ->
                        new AmenityNotFoundException(
                                "Amenity not found with id: " + id));

        amenityRepository.delete(amenity);
    }
}