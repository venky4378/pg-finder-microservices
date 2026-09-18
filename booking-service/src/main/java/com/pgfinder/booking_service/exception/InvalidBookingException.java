package com.pgfinder.booking_service.exception;

public class InvalidBookingException  extends RuntimeException{
    public InvalidBookingException(String s){
        super(s);
    }
}
