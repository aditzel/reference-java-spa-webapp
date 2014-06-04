package com.allanditzel.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Application specific representation of an operation not being allowed.
 *
 * @author Allan Ditzel
 * @since 0.4
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Not Allowed")
public class AccessNotAllowedException extends RuntimeException {
    public AccessNotAllowedException() {
    }

    public AccessNotAllowedException(String message) {
        super(message);
    }

    public AccessNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessNotAllowedException(Throwable cause) {
        super(cause);
    }

    public AccessNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
