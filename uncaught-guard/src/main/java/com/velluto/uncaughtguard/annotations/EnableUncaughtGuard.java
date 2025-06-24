package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.advices.UncaughtGuardRestControllerAdvice;
import com.velluto.uncaughtguard.registrars.UncaughtGuardRegistrar;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ UncaughtGuardRegistrar.class, UncaughtGuardRestControllerAdvice.class })
public @interface EnableUncaughtGuard {
    Class<? extends UncaughtGuardLoggingStrategy>[] loggingStrategies() default { };

    Class<? extends RuntimeException>[] excludedExceptions() default {};

    String httpResponseErrorMessage() default "Internal Server Error: an unexpected error occurred";

    String logErrorMessage() default "An unhandled exception has been caught";

    boolean keepThrowingExceptions() default false;

    boolean enableLogRequestBody() default true;
}
