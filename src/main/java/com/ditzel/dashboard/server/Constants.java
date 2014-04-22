package com.ditzel.dashboard.server;

/**
 * Created by Allan on 4/3/2014.
 */
public class Constants {
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String USER_AGENT_FINGERPRINT = "_userAgentFingerprint";
    public static final String CSRF_TOKEN_KEY = "_csrf";

    public static String USER_ROLE = "https://api.stormpath.com/v1/groups/70f4Mm3bMyCc9Z8ocCMisp";
    public static String ADMIN_ROLE = "https://api.stormpath.com/v1/groups/6hKi5x6hBS2uZ36jLwSN8R";
    public static String STORMPATH_APPLICATION_URL = "https://api.stormpath.com/v1/applications/5AnjVUXhEZ51vTjZuCnXJn";
    public static String STORMPATH_API_KEY_LOCATION = System.getProperty("user.home") + "/.stormpath/apiKey.properties";

}
