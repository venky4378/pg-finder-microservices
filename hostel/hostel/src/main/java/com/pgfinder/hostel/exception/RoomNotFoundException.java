package com.pgfinder.hostel.exception;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String s){
        super(s);
    }
}
