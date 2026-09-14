package com.pgfinder.hostel.controller;

import com.pgfinder.hostel.dto.AmenityDto;
import com.pgfinder.hostel.service.AmenityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping
    public ResponseEntity<AmenityDto> createAmenity(@RequestBody AmenityDto amenityDto) {

        AmenityDto response = amenityService.createAmenity(amenityDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AmenityDto>> getAllAmenities() {

        List<AmenityDto> response = amenityService.getAllAmenities();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDto> getAmenityById(@PathVariable Long id) {

        AmenityDto response =amenityService.getAmenityById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmenityDto> updateAmenity(@PathVariable Long id,
            @RequestBody AmenityDto amenityDto) {

        AmenityDto response = amenityService.updateAmenity(id, amenityDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.ok("Amenity deleted successfully");
    }
}