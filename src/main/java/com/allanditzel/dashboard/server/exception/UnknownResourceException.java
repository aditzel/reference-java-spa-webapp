/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allanditzel.dashboard.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Application specific representation of a resource not being found within the system.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Unknown Resource")
public class UnknownResourceException extends RuntimeException {
    public UnknownResourceException() {
    }

    public UnknownResourceException(String message) {
        super(message);
    }

    public UnknownResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownResourceException(Throwable cause) {
        super(cause);
    }

    public UnknownResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
