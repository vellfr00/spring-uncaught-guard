package com.velluto.uncaughtguard.registrars;

import com.velluto.uncaughtguard.annotations.EnableUncaughtGuard;
import com.velluto.uncaughtguard.filters.UncaughtGuardContentRequestCachingFilter;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UncaughtGuardRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(UncaughtGuardRegistrar.class);

    @Override
    @SuppressWarnings("unchecked")
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableUncaughtGuard.class.getName(), false)
        );

        logger.debug("Registering UncaughtGuard properties.");

        Class<? extends UncaughtGuardLoggingStrategy>[] strategies = (Class<? extends UncaughtGuardLoggingStrategy>[]) attrs.getClassArray("loggingStrategies");
        if(strategies.length == 0) {
            strategies = new Class[] { UncaughtGuardSystemErrorLoggingStrategy.class };
            logger.debug("Retrieved empty logging strategies property, setting to default {}", UncaughtGuardSystemErrorLoggingStrategy.class.getSimpleName());
        }
        
        Class<? extends RuntimeException>[] excludedExceptions = (Class<? extends RuntimeException>[]) attrs.getClassArray("excludedExceptions");
        String httpResponseErrorMessage = attrs.getString("httpResponseErrorMessage");
        String logErrorMessage = attrs.getString("logErrorMessage");
        boolean keepThrowingExceptions = attrs.getBoolean("keepThrowingExceptions");
        boolean enableLogRequestBody = attrs.getBoolean("enableLogRequestBody");

        // Register the properties bean
        RootBeanDefinition def = new RootBeanDefinition(UncaughtGuardProperties.class);
        def.getPropertyValues().add("loggingStrategies", strategies);
        def.getPropertyValues().add("excludedExceptions", excludedExceptions);
        def.getPropertyValues().add("httpResponseErrorMessage", httpResponseErrorMessage);
        def.getPropertyValues().add("logErrorMessage", logErrorMessage);
        def.getPropertyValues().add("keepThrowingExceptions", keepThrowingExceptions);
        def.getPropertyValues().add("enableLogRequestBody", enableLogRequestBody);
        registry.registerBeanDefinition("uncaughtGuardProperties", def);
        logger.debug("""
                        Successfully registered UncaughtGuard properties.
                        Registered properties:
                        
                        loggingStrategies        : {}
                        excludedExceptions       : {}
                        httpResponseErrorMessage : {}
                        logErrorMessage          : {}
                        keepThrowingExceptions   : {}
                        enableLogRequestBody     : {}
                        """,
                Arrays.stream(strategies).map(Class::getSimpleName).collect(Collectors.joining(",")),
                Arrays.stream(excludedExceptions).map(Class::getSimpleName).collect(Collectors.joining(",")),
                httpResponseErrorMessage,
                logErrorMessage,
                keepThrowingExceptions,
                enableLogRequestBody
        );

        // Register the logging strategies implementation beans
        for (Class<? extends UncaughtGuardLoggingStrategy> strategyClass : strategies) {
            RootBeanDefinition beanDef = new RootBeanDefinition(strategyClass);
            String beanName = strategyClass.getSimpleName().substring(0, 1).toLowerCase()
                    + strategyClass.getSimpleName().substring(1);
            registry.registerBeanDefinition(beanName, beanDef);
            logger.debug("Successfully registered the specified logging strategy: {}", strategyClass.getName());
        }

        // Register the Request Content Caching filter, if enabled
        if (enableLogRequestBody) {
            RootBeanDefinition beanDef = new RootBeanDefinition(UncaughtGuardContentRequestCachingFilter.class);
            registry.registerBeanDefinition("uncaughtGuardContentRequestCachingFilter", beanDef);
            logger.debug("Successfully enabled request body logging, by registering the request content caching filter");
        }
    }
}
