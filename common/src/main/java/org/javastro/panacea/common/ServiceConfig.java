package org.javastro.panacea.common;

/**
 * Config property names used to bind inter-service REST client URLs.
 */
public final class ServiceConfig {
    public static final String APP_REGISTRY_URL = "panacea.app-registry.url";
    public static final String PLATFORM_REGISTRY_URL = "panacea.execution-platform-registry.url";
    public static final String JOB_QUEUE_URL = "panacea.job-queue.url";

    private ServiceConfig() {
    }
}
