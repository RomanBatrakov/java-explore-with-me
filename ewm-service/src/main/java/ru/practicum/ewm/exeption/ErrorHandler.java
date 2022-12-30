package ru.practicum.ewm.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiError> handlerNoSuchElement(NoSuchElementException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(exception.getLocalizedMessage())
                        .reason("The required object was not found: " + request.getDescription(false))
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ApiError> handlerBadValidation(ValidationException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .message(exception.getLocalizedMessage())
                        .reason("Integrity constraint has been violated: " +
                                request.getDescription(false))
                        .status(HttpStatus.CONFLICT)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handlerBadRequest(IllegalArgumentException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .errors(List.of(exception.getClass().getName()))
                        .message(exception.getLocalizedMessage())
                        .reason("For the requested operation the conditions are not met: " +
                                request.getDescription(false))
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handlerBadRequestParameter(
            MissingServletRequestParameterException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .errors(List.of(exception.getClass().getName()))
                        .message(exception.getLocalizedMessage())
                        .reason("For the requested operation the conditions are not met: " +
                                request.getDescription(false))
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
    }
}