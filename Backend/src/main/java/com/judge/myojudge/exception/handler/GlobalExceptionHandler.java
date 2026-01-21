package com.judge.myojudge.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.judge.myojudge.exception.*;
import com.judge.myojudge.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
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
            String message;
            String field="";
            if(error instanceof FieldError fieldError){
                message= error.getDefaultMessage();
                Object target = exception.getBindingResult().getTarget();
                Class<?> targetClass = (target != null) ? target.getClass() : null;
                field = resolveJsonFieldName(targetClass,fieldError.getField());
            }
            else {
                message= error.getObjectName()+" : "+error.getDefaultMessage();
            }

            ErrorResponse errorResponse= ErrorResponse.builder()
                            .field(field)
                            .error(message)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .path(request.getRequestURI())
                            .message(message)
                            .build();
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

    @ExceptionHandler(ImageSizeLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileExitException(ImageSizeLimitExceededException exception,HttpServletRequest request) {
        log.error("Image Size Limit Exceeded Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(buildError(HttpStatus.OK, exception.getMessage(), request.getRequestURI()));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileExitException(MaxUploadSizeExceededException exception,HttpServletRequest request) {
        log.error("Max File Size Limit Exceeded Exception: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(buildError(HttpStatus.OK, "This file size is too large", request.getRequestURI()));
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileExitException(FileNotFoundException exception,HttpServletRequest request) {
        log.error("File Not Found Exception: {}", exception.getMessage());
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
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildError(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to perform this action!",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthenticatedUserException(AuthenticationException exception,
                                                                            HttpServletRequest request) {
        log.error("Unauthenticated Exception: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized access. Please login first",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongHttpMethodHitException(HttpRequestMethodNotSupportedException exception,
                                                                           HttpServletRequest request) {
        log.error("Wrong Http Method Exception: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(buildError(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "HTTP method not supported for this endpoint",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(NoHandlerFoundException exception,
                                                                   HttpServletRequest request) {
        log.error("Endpoint Not Found Exception: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        HttpStatus.NOT_FOUND,
                        "This endpoint isn't found",
                        request.getRequestURI()
                ));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleEmptyRequestBodyException(HttpMessageNotReadableException exception,
                                                                   HttpServletRequest request) {
        log.error("Request Body Not Found Exception: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        "Request body is required and must be valid JSON",
                        request.getRequestURI()
                ));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException(Exception exception,
                                                             HttpServletRequest request) {
        log.error("From Exception class: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error", request.getRequestURI()));
    }

    private ErrorResponse buildError(HttpStatus status,String message, String path) {

        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );
    }

    /**
     * Resolves @JsonProperty value if present,
     * otherwise falls back to Java field name
     */
    private String resolveJsonFieldName(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

            if (jsonProperty != null && !jsonProperty.value().isEmpty()) {
                return jsonProperty.value();
            }
        } catch (Exception ignored) {}

        return fieldName;
    }

}
