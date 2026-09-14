package com.pgfinder.hostel.exception;

public class BedNotFoundException extends RuntimeException {

    public BedNotFoundException(String message) {
        super(message);
    }
}