package com.ditzel.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main configuration entry point for web application.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@Configuration
@Import({
        PropertyConfig.class
})
public class WebAppConfig {
}
