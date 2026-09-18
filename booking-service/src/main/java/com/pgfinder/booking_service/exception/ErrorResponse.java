package com.pgfinder.booking_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private int status;
    private String message;
    private String timeStamp;
}
