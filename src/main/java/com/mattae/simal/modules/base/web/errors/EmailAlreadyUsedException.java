package com.mattae.simal.modules.base.web.errors;

public class EmailAlreadyUsedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email is already in use!");
    }
}
