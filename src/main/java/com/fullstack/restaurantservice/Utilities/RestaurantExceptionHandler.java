package com.fullstack.restaurantservice.Utilities;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;


@Slf4j
@ControllerAdvice
public class RestaurantExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String entityNotFoundHandler(@NotNull EntityNotFoundException exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return exception.getCause() + ", entity not found | " + exception.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public String executionExceptionHandler(@NotNull Exception exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return exception.getCause() + ", entity or service endpoint not found | " + exception.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(@NotNull Exception exception){
        log.error(String.format("Exception: %s, message: %s", exception.getCause(), exception.getMessage()));
        return exception.getCause() + ", " + exception.getMessage();
    }
}