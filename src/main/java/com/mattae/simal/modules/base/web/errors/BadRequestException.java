package com.mattae.simal.modules.base.web.errors;


public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String entityName;

    private String errorKey;

    public BadRequestException(String message) {
        super(message);
    }
}
