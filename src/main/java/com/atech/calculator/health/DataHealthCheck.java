package com.atech.calculator.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.time.Instant;

@Liveness
public class DataHealthCheck implements HealthCheck {

    public static final String CALCULATOR_HEALTH_CHECK = "[Calculator-Health-Check]";

    private Instant currentInstant = Instant.now();

    public void updateCurrentInstant() {
        this.currentInstant = Instant.now();
    }

    @Override
    public HealthCheckResponse call(){
        updateCurrentInstant();
        return HealthCheckResponse.named(CALCULATOR_HEALTH_CHECK)
                .withData("[get-calculator.instant]", currentInstant.toString())
                .up()
                .build();
    }
}
