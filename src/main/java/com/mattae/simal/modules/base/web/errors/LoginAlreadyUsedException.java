package com.mattae.simal.modules.base.web.errors;

public class LoginAlreadyUsedException extends BadRequestException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("Login name already used!");
    }
}
