package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import jakarta.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

/**
 * Abstract class for logging uncaught exceptions to a REST endpoint.
 * This class extends the UncaughtGuardLoggingStrategy and provides
 * functionality to send exception traces to a specified REST endpoint.
 * Developers must implement the `restEndpoint()` method to provide the
 * endpoint URL where the exceptions will be logged.
 * When an uncaught exception occurs, a POST request is made to the
 * specified REST endpoint with the entire exception trace as the request body.
 */
public abstract class UncaughtGuardRestLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private static final Logger logger = Logger.getLogger(UncaughtGuardRestLoggingStrategy.class.getName());

    private String restEndpoint;
    private RestTemplate restTemplate;

    /**
     * Returns the REST endpoint where the uncaught exceptions will be logged.
     * Developers must implement this method to provide the endpoint URL.
     *
     * @return the REST endpoint URL as a String
     */
    public abstract String restEndpoint();

    /**
     * Initializes the REST logging strategy by loading and validating the REST endpoint.
     * It then creates a new instance of RestTemplate for making HTTP requests.
     * This method is called after the bean is constructed and dependency injection is complete.
     */
    @PostConstruct
    private void init() {
        loadAndValidateRestEndpoint();
        this.restTemplate = new RestTemplate();
    }

    /**
     * Loads the REST endpoint from the value returned by the `restEndpoint()` method.
     * Validates that the endpoint is not null or empty.
     * If the endpoint is invalid, an IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if the endpoint is null or empty
     */
    private void loadAndValidateRestEndpoint() {
        String endpoint = restEndpoint();
        if (endpoint == null || endpoint.isEmpty())
            throw new IllegalArgumentException("Rest endpoint must not be null or empty");

        this.restEndpoint = endpoint;
        logger.info("Successfully initialized Rest Logging Strategy, POST requests will be sent to endpoint: " + restEndpoint);
    }

    @Override
    protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
        restTemplate.postForEntity(restEndpoint, exceptionTrace, Void.class);
    }
}
