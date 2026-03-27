package com.buildledger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BuildLedgerAccessDeniedException extends RuntimeException {
    public BuildLedgerAccessDeniedException(String message) {
        super(message);
    }
}
