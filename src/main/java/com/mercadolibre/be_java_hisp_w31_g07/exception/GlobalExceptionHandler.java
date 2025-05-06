package com.mercadolibre.be_java_hisp_w31_g07.exception;

import com.mercadolibre.be_java_hisp_w31_g07.dto.GlobalExceptionHandlerDto;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<GlobalExceptionHandlerDto> badRequest(BadRequest e) {
        GlobalExceptionHandlerDto exceptionDto = new GlobalExceptionHandlerDto(e.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalExceptionHandlerDto> typeMismatch(MethodArgumentTypeMismatchException e) {
        String param = e.getName();
        GlobalExceptionHandlerDto dto = new GlobalExceptionHandlerDto(ErrorMessagesUtil.noValidParameter(param));
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }
}