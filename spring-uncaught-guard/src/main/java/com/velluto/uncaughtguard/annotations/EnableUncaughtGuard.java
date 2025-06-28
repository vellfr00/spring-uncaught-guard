package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.advices.UncaughtGuardRestControllerAdvice;
import com.velluto.uncaughtguard.registrars.UncaughtGuardRegistrar;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables the UncaughtGuard exception handling mechanism for a Spring application.
 * <p>
 * This annotation allows customization of logging strategies, excluded exceptions,
 * error messages, and other behaviors related to uncaught exception handling.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({UncaughtGuardRegistrar.class, UncaughtGuardRestControllerAdvice.class})
public @interface EnableUncaughtGuard {

    /**
     * List of logging strategies to be applied when an uncaught exception occurs.
     * By default, uses {@link UncaughtGuardSystemErrorLoggingStrategy}.
     *
     * @return array of logging strategy classes
     */
    Class<? extends UncaughtGuardLoggingStrategy>[] loggingStrategies() default {UncaughtGuardSystemErrorLoggingStrategy.class};

    /**
     * List of exception types to be excluded from UncaughtGuard handling.
     *
     * @return array of exception classes to exclude
     */
    Class<? extends RuntimeException>[] excludedExceptions() default {};

    /**
     * Error message to be returned in the HTTP response when an uncaught exception is handled.
     *
     * @return HTTP response error message
     */
    String httpResponseErrorMessage() default "Internal Server Error: an unexpected error occurred";

    /**
     * Error message to be logged when an uncaught exception is handled.
     *
     * @return log error message
     */
    String logErrorMessage() default "An unhandled exception has been caught";

    /**
     * If true, rethrows the exception after handling it.
     * By default, this is set to false.
     * If you set this to true, the exception will be rethrown after being handled by the UncaughtGuard.
     * This means that the exception body will not be managed by the UncaughtGuard, and the default Spring Boot error handling will take over.
     * You will therefore lose the traceId in the returned response, which is a key feature of the UncaughtGuard to enable simple tracing of errors in a distributed system.
     *
     * @return true to rethrow exceptions, false to suppress
     */
    boolean keepThrowingExceptions() default false;

    /**
     * If true, enables logging of the HTTP request body when an exception is handled.
     * By default, this is set to true.
     * Keep in mind that this will register a filter that caches the request body and this can have an impact on performance.
     * if you do not want this behavior, set this to false, but then you will not be able to log the request body in case of an exception.
     *
     * @return true to enable request body logging, false otherwise
     */
    boolean enableLogRequestBody() default true;
}
