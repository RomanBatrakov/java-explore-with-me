package ru.practicum.ewm.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiError> handlerNoSuchElement(NoSuchElementException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(ex.getLocalizedMessage())
                        .reason("The required object was not found: " + request.getDescription(false))
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ApiError> handlerBadValidation(ValidationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .message(ex.getLocalizedMessage())
                        .reason("Integrity constraint has been violated: " +
                                request.getDescription(false))
                        .status(HttpStatus.CONFLICT)
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handlerBadRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .errors(List.of(ex.getClass().getName()))
                        .message(ex.getLocalizedMessage())
                        .reason("For the requested operation the conditions are not met: " +
                                request.getDescription(false))
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = ((FieldError) error).getField() + error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .errors(errors)
                        .message(ex.getLocalizedMessage())
                        .reason("For the requested operation the conditions are not met: " +
                                request.getDescription(false))
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
    }
}