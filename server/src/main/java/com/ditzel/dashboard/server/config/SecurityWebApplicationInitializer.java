package com.ditzel.dashboard.server.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Web application initializer that sets up the filter chain in order for spring security to intercept incoming requests.
 * Works in conjunctin with {@link com.ditzel.dashboard.server.config.WebappInitializer}.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
