package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.exceptions.UncaughtGuardMethodParametersEnrichedRuntimeException;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.utils.UncaughtGuardExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

/**
 * Aspect that enriches the parameters of methods in Spring components
 * when a RuntimeException is thrown, allowing for better debugging and
 * error handling.
 * </p>
 * This aspect captures the method parameters passed to the method
 * that threw the exception, and wraps the original RuntimeException
 * in a custom exception that includes the method signature and parameters.
 * </p>
 * It is applied to methods within classes annotated with
 * @RestController, @Service, or @Repository.
 */
@Aspect
public class UncaughtGuardMethodParametersEnricherAdvice {
    private final Logger logger = Logger.getLogger(UncaughtGuardMethodParametersEnricherAdvice.class.getName());

    @Autowired
    private UncaughtGuardProperties properties;
    @Autowired
    private UncaughtGuardExceptionUtils exceptionUtils;

    @AfterThrowing(
            pointcut = "(@within(org.springframework.web.bind.annotation.RestController) || " +
                    "@within(org.springframework.stereotype.Service) || " +
                    "@within(org.springframework.stereotype.Repository))",
            throwing = "throwable"
    )
    public void captureMethodParameters(JoinPoint joinPoint, Throwable throwable){
        String className = joinPoint.getTarget().getClass().getName();
        String methodSignature = joinPoint.getSignature().toString();
        Object[] methodArgs = joinPoint.getArgs();

        logger.fine("Triggered parameters enrichment advice for method: " + methodSignature + " in class: " + className);

        if(!(throwable instanceof RuntimeException runtimeException)) {
            logger.fine("Thrown exception from method: " + methodSignature + " in class: " + className + " is not a RuntimeException, no enrichment applied");
            return;
        }

        if (exceptionUtils.isExceptionExcluded(runtimeException))
            return;

        logger.fine("Enriching parameters for exception thrown from method: " + methodSignature + " in class: " + className);
        UncaughtGuardMethodParametersEnrichedRuntimeException enrichedRuntimeException = new UncaughtGuardMethodParametersEnrichedRuntimeException(
                runtimeException,
                methodSignature,
                methodArgs
        );

        throw enrichedRuntimeException;
    }
}