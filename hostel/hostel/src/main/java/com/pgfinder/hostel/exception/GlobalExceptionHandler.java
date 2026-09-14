package com.pgfinder.hostel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HostelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHostel(HostelNotFoundException ex){
        ErrorResponse errorResponse= new ErrorResponse(404,ex.getMessage(), LocalDateTime.now().toString());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler(AmenityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAmenityNotFound(AmenityNotFoundException ex) {

        ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage(), LocalDateTime.now().toString());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler(BedNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBedNotFound(BedNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage(), LocalDateTime.now().toString());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler(BedAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBedAlreadyExists(BedAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(409, ex.getMessage(), LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


}
