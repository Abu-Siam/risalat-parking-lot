package com.callicoder.goparking.exceptions;

public class InvalidLeaveSlotException extends RuntimeException{
    public InvalidLeaveSlotException(String message) {
        super(message);
    }
}
