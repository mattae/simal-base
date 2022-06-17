package com.mattae.simal.modules.base.web.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class RecordNotFoundException extends AbstractThrowableProblem {
    public RecordNotFoundException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, "Bad request", Status.NOT_FOUND, message);
    }
}
