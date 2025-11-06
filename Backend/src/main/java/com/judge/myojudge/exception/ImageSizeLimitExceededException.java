package com.judge.myojudge.exception;

public class ImageSizeLimitExceededException extends RuntimeException {
    public ImageSizeLimitExceededException(String message) {
        super(message);
    }
}
