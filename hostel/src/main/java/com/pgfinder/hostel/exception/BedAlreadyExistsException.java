package com.pgfinder.hostel.exception;

public class BedAlreadyExistsException extends RuntimeException {

    public BedAlreadyExistsException(String message) {
        super(message);
    }
}