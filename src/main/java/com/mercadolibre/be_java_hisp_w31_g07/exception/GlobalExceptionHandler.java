package com.mercadolibre.be_java_hisp_w31_g07.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.mercadolibre.be_java_hisp_w31_g07.dto.GlobalExceptioHandlerDto;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<?> badRequest(BadRequest e) {
        GlobalExceptioHandlerDto exceptionDto = new GlobalExceptioHandlerDto(e.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFound(NotFoundException e) {
        GlobalExceptioHandlerDto exceptionDto = new GlobalExceptioHandlerDto(e.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> typeMismatch(MethodArgumentTypeMismatchException e) {
        String param = e.getName();
        String message = "Parameter '" + param + "' is invalid."; 
        GlobalExceptioHandlerDto dto = new GlobalExceptioHandlerDto(message);
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}