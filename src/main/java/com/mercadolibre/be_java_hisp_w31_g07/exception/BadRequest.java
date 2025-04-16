package com.mercadolibre.be_java_hisp_w31_g07.exception;

public class BadRequest extends RuntimeException{
    public BadRequest(String message) {
        super(message);
    }
}
