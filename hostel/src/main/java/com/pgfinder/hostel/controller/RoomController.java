package com.pgfinder.hostel.controller;

import com.pgfinder.hostel.dto.RoomDto;
import com.pgfinder.hostel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/hostel/{hostelId}")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hostelId, @Valid @RequestBody RoomDto roomDto) {
        RoomDto response = roomService.createRoom(hostelId, roomDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomDto> response = roomService.getAllRooms();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        RoomDto response = roomService.getRoomById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @Valid@RequestBody RoomDto roomDto) {
        RoomDto response = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }
}