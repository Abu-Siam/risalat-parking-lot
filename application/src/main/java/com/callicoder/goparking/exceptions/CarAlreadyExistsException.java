package com.callicoder.goparking.exceptions;

public class CarAlreadyExistsException extends RuntimeException{
    public CarAlreadyExistsException(String message) {
        super(message);
    }
}
