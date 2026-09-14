package com.pgfinder.hostel.exception;

public class RoomAlreadyExistsException extends RuntimeException{
    public RoomAlreadyExistsException(String s){
        super(s);
    }
}
