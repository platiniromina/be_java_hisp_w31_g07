package com.mercadolibre.be_java_hisp_w31_g07.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
