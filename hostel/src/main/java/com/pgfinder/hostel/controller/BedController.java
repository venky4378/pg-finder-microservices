package com.pgfinder.hostel.controller;

import com.pgfinder.hostel.dto.BedDto;
import com.pgfinder.hostel.service.BedService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beds")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<BedDto> createBed(@PathVariable Long roomId, @Valid @RequestBody BedDto bedDto) {
        BedDto response = bedService.createBed(roomId, bedDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BedDto>> getAllBeds() {
        List<BedDto> response = bedService.getAllBeds();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BedDto> getBedById(@PathVariable Long id) {
        BedDto response = bedService.getBedById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hostel/{hostelId}/bed/{bedId}")
    public ResponseEntity<BedDto> getBedByHostelId(@PathVariable Long hostelId, @PathVariable Long bedId) {
        return ResponseEntity.ok(bedService.getBedByHostelId(hostelId, bedId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BedDto> updateBed(@PathVariable Long id, @Valid @RequestBody BedDto bedDto) {
        BedDto response = bedService.updateBed(id, bedDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BedDto> updateBedStatus(@PathVariable Long id, @RequestParam String status) {
        BedDto response = bedService.updateBedStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBed(@PathVariable Long id) {
        bedService.deleteBed(id);
        return ResponseEntity.ok("Bed deleted successfully");
    }
}