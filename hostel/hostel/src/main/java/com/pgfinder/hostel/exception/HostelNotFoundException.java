package com.pgfinder.hostel.exception;

public class HostelNotFoundException extends RuntimeException{
    public HostelNotFoundException(String message){
        super(message);
    }
}
