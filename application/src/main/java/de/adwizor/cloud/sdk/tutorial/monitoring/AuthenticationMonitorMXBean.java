package de.adwizor.cloud.sdk.tutorial.monitoring;

public interface AuthenticationMonitorMXBean {
    String clearLogOfFailedAuthenticationRequestsByUser(String name);
    int getNumberOfFailedAuthenticationRequests();
}
