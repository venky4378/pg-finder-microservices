package com.pgfinder.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hostel-service")
public interface HostelClient {

    @GetMapping("/api/v1/hostels/{id}")
    HostelClientResponseDto getHostelById(@PathVariable Long id);

    @GetMapping("/api/v1/beds/{id}")
    BedClientResponseDto getBedById(@PathVariable Long id);

    @GetMapping("/api/v1/beds/hostel/{hostelId}/bed/{bedId}")
    BedClientResponseDto getBedByHostelId(@PathVariable Long hostelId, @PathVariable Long bedId);

    @PutMapping("/api/v1/beds/{id}/status")
    BedClientResponseDto updateBedStatus(@PathVariable("id") Long id, @RequestParam("status") String status);

}