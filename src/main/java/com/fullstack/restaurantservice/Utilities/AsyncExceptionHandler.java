package com.fullstack.restaurantservice.Utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.lang.reflect.Method;

//only catches exceptions in methods with void return value
@Slf4j
@ControllerAdvice
public class AsyncExceptionHandler  implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error(ex.getCause() + ", " + ex.getMessage());
        log.error("Method name: " + method.getName());
        StringBuilder parameters = new StringBuilder();
        for(Object param : params){
            parameters.append(param).append(" ");
        }
        log.error("Parameters: " + parameters);
    }
}
