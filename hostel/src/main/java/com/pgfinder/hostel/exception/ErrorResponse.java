package com.pgfinder.hostel.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private int status;
    private String message;
    private String time;

}
