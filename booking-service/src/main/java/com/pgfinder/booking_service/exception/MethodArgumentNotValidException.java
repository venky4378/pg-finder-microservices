package com.pgfinder.booking_service.exception;

public class MethodArgumentNotValidException extends RuntimeException{
    public MethodArgumentNotValidException(String s){
        super (s);
    }
}
