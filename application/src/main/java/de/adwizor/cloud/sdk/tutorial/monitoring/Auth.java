package de.adwizor.cloud.sdk.tutorial.monitoring;

public class Auth {

    public static String clearLogOfFailedAuthenticationRequestsByUser(String name) {
        return "Clear failed login attempts by " + name;
    }

    public static int getNumberOfFailedAuthenticationRequests() {
        return 0;
    }
}
