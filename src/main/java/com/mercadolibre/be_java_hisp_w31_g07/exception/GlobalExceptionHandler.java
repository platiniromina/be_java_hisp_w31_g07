package com.mercadolibre.be_java_hisp_w31_g07.exception;

import com.mercadolibre.be_java_hisp_w31_g07.dto.GlobalExceptioHandlerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<?> notFound(BadRequest e){
        GlobalExceptioHandlerDto exceptionDto = new GlobalExceptioHandlerDto(e.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
}
