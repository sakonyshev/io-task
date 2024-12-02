package com.io.iotask.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error id={}, message={}, stacktrace={}", errorId, ex.getMessage(), ex.getStackTrace());

        HttpStatusCode statusCode = ex instanceof ResponseStatusException statusException ? statusException.getStatusCode() : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>("There was an error on server side." +
                " Try again or ask support team to resolve the problem. " +
                "Error id:" + errorId, statusCode);
    }
}
