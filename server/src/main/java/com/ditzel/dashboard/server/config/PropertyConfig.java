package com.ditzel.dashboard.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for properties.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@Configuration
@PropertySource("classpath:default.properties")
public class PropertyConfig {
}
