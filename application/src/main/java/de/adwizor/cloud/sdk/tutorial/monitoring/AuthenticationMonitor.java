package de.adwizor.cloud.sdk.tutorial.monitoring;

import com.sap.cloud.sdk.cloudplatform.monitoring.JmxMonitor;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class AuthenticationMonitor extends JmxMonitor implements AuthenticationMonitorMXBean {

    private static final AuthenticationMonitor instance = new AuthenticationMonitor();

    @Override
    public String clearLogOfFailedAuthenticationRequestsByUser(String name) {
        return Auth.clearLogOfFailedAuthenticationRequestsByUser(name);
    }

    @Override
    public int getNumberOfFailedAuthenticationRequests() {
        return Auth.getNumberOfFailedAuthenticationRequests();
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        instance.registerJmxBean();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        instance.unregisterJmxBean();
    }
}