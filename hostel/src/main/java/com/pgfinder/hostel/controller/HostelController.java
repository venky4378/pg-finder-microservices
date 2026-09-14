package com.pgfinder.hostel.controller;

import com.pgfinder.hostel.dto.HostelRequestDto;
import com.pgfinder.hostel.dto.HostelResponseDto;
import com.pgfinder.hostel.service.HostelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hostels")
public class HostelController {

    private final HostelService hostelService;
    public HostelController(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @PostMapping
    public ResponseEntity<HostelResponseDto> createHostel(
            @Valid @RequestBody HostelRequestDto hostelRequestDto) {

        HostelResponseDto response = hostelService.createHostel(hostelRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HostelResponseDto>> getAllHostels() {
        List<HostelResponseDto> response = hostelService.getAllHostels();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HostelResponseDto> getHostelById(@PathVariable Long id) {

        HostelResponseDto response = hostelService.getHostelById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HostelResponseDto> updateHostel(@PathVariable Long id, @Valid @RequestBody HostelRequestDto hostelRequestDto) {

        HostelResponseDto response = hostelService.updateHostel(id, hostelRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHostel(@PathVariable Long id) {

        hostelService.deleteHostel(id);
        return ResponseEntity.ok("Hostel deleted successfully");
    }
}