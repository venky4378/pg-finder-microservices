package com.pgfinder.hostel.service;

import com.pgfinder.hostel.dto.HostelRequestDto;
import com.pgfinder.hostel.dto.HostelResponseDto;

import java.util.List;

public interface HostelService {

    HostelResponseDto createHostel(HostelRequestDto hostelRequestDto);

    List<HostelResponseDto> getAllHostels();

    HostelResponseDto getHostelById(Long id);

    HostelResponseDto updateHostel(
            Long id,
            HostelRequestDto hostelRequestDto
    );

    void deleteHostel(Long id);
}