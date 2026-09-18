package com.pgfinder.hostel.service;

import com.pgfinder.hostel.dto.BedDto;

import java.util.List;

public interface BedService {

    BedDto createBed(Long roomId, BedDto bedDto);

    List<BedDto> getAllBeds();

    BedDto getBedById(Long id);

    BedDto updateBed(Long id, BedDto bedDto);

    void deleteBed(Long id);

    BedDto getBedByHostelId(Long hostelId,Long bedId);
}