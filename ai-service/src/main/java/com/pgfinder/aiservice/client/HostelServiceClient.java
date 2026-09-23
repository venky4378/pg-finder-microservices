package com.pgfinder.aiservice.client;

import com.pgfinder.aiservice.dto.HostelSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "hostel-service")
public interface HostelServiceClient {

    @GetMapping("/api/v1/hostels")
    List<HostelSummaryDto> getAllHostels();
}
