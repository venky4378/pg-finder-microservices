package com.pgfinder.booking_service.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;

@Data
public class BedClientResponseDto {
    private Long id;
    private String bedNumber;
    private String status;

}
