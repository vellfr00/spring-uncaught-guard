package com.velluto.uncaughtguard.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class to enable asynchronous processing in the application.
 * This is necessary for the UncaughtGuard to trigger logging of uncaught exceptions
 * in an asynchronous manner so that the HTTP request-response flow is not blocked.
 */
@Configuration
@EnableAsync
public class UncaughtGuardAsyncConfiguration {
}