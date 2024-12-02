package com.io.iotask.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception ex) {
        UUID errorId = UUID.randomUUID();
        log.error("Error id={}, message={}, stacktrace={}", new Object[]{errorId, ex.getMessage(), ex.getStackTrace()});
        return new ResponseEntity("There was an error on server side. Try again or ask support team to resolve the problem. " +
                "Error id:" + errorId, HttpStatus.BAD_REQUEST);
    }
}
