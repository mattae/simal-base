package com.mattae.simal.modules.base.web.errors;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
