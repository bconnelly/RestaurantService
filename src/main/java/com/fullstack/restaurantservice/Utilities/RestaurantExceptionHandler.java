package com.fullstack.restaurantservice.Utilities;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;

@Slf4j
@ControllerAdvice
public class RestaurantExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundHandler(@NotNull EntityNotFoundException exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        if(exception.getMessage().isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("entity not found");
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getCause() + ", entity not found | " + exception.getMessage());
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<String> executionExceptionHandler(@NotNull Exception exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));

        if(exception.getCause().getClass().equals(HttpClientErrorException.NotFound.class))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getCause() + ", entity or service endpoint not found | " + exception.getMessage());
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getCause() + ", exception thrown in thread | " + exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(@NotNull Exception exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getCause() + ", " + exception.getMessage());
    }
}