package com.pgfinder.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.message.StringFormattedMessage;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private int status;
    private String message;
    private String timeStamp;
}
