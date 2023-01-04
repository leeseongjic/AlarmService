package com.enums;

public enum HealthCheckEnum {

    SERVICE("service");

    private final String healthCheckId;

    HealthCheckEnum(String healthCheckId) {
        this.healthCheckId = healthCheckId;
    }

    public String getHealthCheckId() {
        return this.healthCheckId;
    }
}
