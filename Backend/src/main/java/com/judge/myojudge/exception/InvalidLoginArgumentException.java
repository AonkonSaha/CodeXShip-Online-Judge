package com.judge.myojudge.exception;

public class InvalidLoginArgumentException extends RuntimeException {
    public InvalidLoginArgumentException(String message) {
        super(message);
    }
}
