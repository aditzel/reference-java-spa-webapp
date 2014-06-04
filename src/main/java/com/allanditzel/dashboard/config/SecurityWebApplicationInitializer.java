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

package com.allanditzel.dashboard.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Web application initializer that sets up the filter chain in order for spring security to intercept incoming requests.
 * Works in conjunctin with {@link com.allanditzel.dashboard.config.WebappInitializer}.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
