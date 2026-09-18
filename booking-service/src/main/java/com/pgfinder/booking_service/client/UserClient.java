package com.pgfinder.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
            name = "user-service",
            url = "${user.service.url}"
)
public interface UserClient {
    @GetMapping("api/v1/users/{id}")
    UserClientResponseDto getUserById(@PathVariable Long id);
}
