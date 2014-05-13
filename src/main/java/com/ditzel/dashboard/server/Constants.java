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

package com.ditzel.dashboard.server;

/**
 * Convenience class to hold frequently used constants.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class Constants {
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String USER_AGENT_FINGERPRINT = "_userAgentFingerprint";
    public static final String CSRF_TOKEN_KEY = "_csrf";

    public static String STORMPATH_API_KEY_LOCATION = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
}
