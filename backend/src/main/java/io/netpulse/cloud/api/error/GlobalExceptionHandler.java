package io.netpulse.cloud.api.error;

import io.netpulse.cloud.domain.exception.DeviceAlreadyExistsException;
import io.netpulse.cloud.domain.exception.DeviceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_BASE_URI = "https://netpulse.io/errors/";

    @ExceptionHandler(DeviceAlreadyExistsException.class)
    public ProblemDetail handleDeviceConflict(DeviceAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "device-conflict"));
        problem.setTitle("Duplicate Network Device");
        problem.setProperty("errorCode", "DEVICE_ALREADY_EXISTS");
        problem.setProperty("conflictField", ex.getConflictField());
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return problem;
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ProblemDetail handleDeviceNotFound(DeviceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "device-not-found"));
        problem.setTitle("Network Device Not Found");
        problem.setProperty("errorCode", "DEVICE_NOT_FOUND");
        problem.setProperty("deviceId", ex.getDeviceId());
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid value",
                        (existing, replacement) -> existing
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request payload failed validation constraints"
        );
        problem.setType(URI.create(ERROR_BASE_URI + "validation-failed"));
        problem.setTitle("Invalid Request Payload");
        problem.setProperty("errorCode", "VALIDATION_FAILED");
        problem.setProperty("fieldErrors", fieldErrors);
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "invalid-query-parameter"));
        problem.setTitle("Invalid Query Parameter");
        problem.setProperty("errorCode", "INVALID_PARAMETER");
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return problem;
    }
}
