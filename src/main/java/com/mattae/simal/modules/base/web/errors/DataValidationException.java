package com.mattae.simal.modules.base.web.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class DataValidationException extends AbstractThrowableProblem {
    public DataValidationException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, "Bad request", Status.BAD_REQUEST, message);
    }
}
