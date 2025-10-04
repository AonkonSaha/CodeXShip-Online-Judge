package com.judge.myojudge.exception.handler;

import com.judge.myojudge.exception.*;
import com.judge.myojudge.response.ApiResponse;
import com.judge.myojudge.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        log.error("Method Argument Not Valid Exception: {}", exception.getMessage());
        List<ErrorResponse> errorResponses=new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String message = ((FieldError) error).getField() + " : "+error.getDefaultMessage();
            ErrorResponse errorResponse = buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
            errorResponses.add(errorResponse);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponses);
    }
    @ExceptionHandler(InvalidUserArgumentException.class)
    public ResponseEntity<ErrorResponse> handleUserValidationException(InvalidUserArgumentException exception,
                                                                     HttpServletRequest request) {
        log.error("User Validation Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(InvalidProblemArgumentException.class)
    public ResponseEntity<ErrorResponse> handleProblemValidationException(InvalidProblemArgumentException exception,
                                                                           HttpServletRequest request) {
        log.error("Problem Validation Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(InvalidTestCaseArgumentException.class)
    public ResponseEntity<ErrorResponse> handleTestCaseValidationException(InvalidTestCaseArgumentException exception,
                                                                       HttpServletRequest request) {
        log.error("TestCase Validation Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleAnyTypeArgumentException(IllegalArgumentException exception,
                                                                       HttpServletRequest request) {
        log.error("IllegalArgument Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserExitException(UserNotFoundException exception,
                                                                 HttpServletRequest request) {
        log.error("User Not Found Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(TestCaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTestcaseExitException(TestCaseNotFoundException exception,
                                                                 HttpServletRequest request) {
        log.error("TestCase Not Found Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(ProblemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProblemExitException(ProblemNotFoundException exception,
                                                                     HttpServletRequest request) {
        log.error("Problem Not Found Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler({InvalidLoginArgumentException.class , BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUserCredentialException(Exception exception,
                                                                       HttpServletRequest request) {
        log.error("User Credential Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedUserException(AccessDeniedException exception,
                                                                       HttpServletRequest request) {
        log.error("Access Denied Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildError(HttpStatus.FORBIDDEN, "You do not have permission to perform this action!", request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleFileException(AuthenticationException exception,
                                                                       HttpServletRequest request) {
        log.error("Authentication Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,"You must be logged in to access this resource!", request.getRequestURI()));
    }

    private ErrorResponse buildError(HttpStatus status, String message, String path) {

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );
    }

}
