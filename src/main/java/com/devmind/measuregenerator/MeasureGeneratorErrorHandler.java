package com.devmind.measuregenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class MeasureGeneratorErrorHandler {

    @ExceptionHandler(value = MeasureException.class)
    public ResponseEntity<?> handleMeasureController(HttpServletRequest request, MeasureException ex){
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

}
