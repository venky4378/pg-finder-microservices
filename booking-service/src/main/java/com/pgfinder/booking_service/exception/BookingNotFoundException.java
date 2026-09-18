package com.pgfinder.booking_service.exception;

public class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(String s){
        super(s);
    }
}
