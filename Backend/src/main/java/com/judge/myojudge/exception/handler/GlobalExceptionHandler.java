package com.judge.myojudge.exception.handler;

import com.judge.myojudge.exception.*;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserArgumentException.class)
    public ResponseEntity<ErrorResponse> handleUserValidationException(InvalidUserArgumentException exception,
                                                                     HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(InvalidTestCaseArgumentException.class)
    public ResponseEntity<ErrorResponse> handleTestCaseValidationException(InvalidTestCaseArgumentException exception,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleAnyTypeArgumentException(IllegalArgumentException exception,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserExitException(UserNotFoundException exception,
                                                                 HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(TestCaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTestcaseExitException(TestCaseNotFoundException exception,
                                                                 HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({InvalidLoginArgumentException.class , BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUserCredentialException(Exception exception,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ErrorResponse> handleFileException(IOException exception,
                                                                       HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String message, String path) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, status);
    }

}
