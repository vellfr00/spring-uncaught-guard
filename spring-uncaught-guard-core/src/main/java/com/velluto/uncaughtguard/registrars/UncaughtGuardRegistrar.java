package com.velluto.uncaughtguard.registrars;

import com.velluto.uncaughtguard.advices.UncaughtGuardMethodParametersEnricherAdvice;
import com.velluto.uncaughtguard.annotations.EnableUncaughtGuard;
import com.velluto.uncaughtguard.filters.UncaughtGuardContentRequestCachingFilter;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UncaughtGuardRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger logger = Logger.getLogger(UncaughtGuardRegistrar.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(
                        EnableUncaughtGuard.class.getName(), false)
        );

        logger.fine("Registering UncaughtGuard properties.");

        Class<? extends UncaughtGuardLoggingStrategy>[] strategies = getLoggingStrategies(attrs);
        Class<? extends RuntimeException>[] excludedExceptions = (Class<? extends RuntimeException>[]) attrs.getClassArray("excludedExceptions");
        String httpResponseErrorMessage = attrs.getString("httpResponseErrorMessage");
        String logErrorMessage = attrs.getString("logErrorMessage");
        boolean keepThrowingExceptions = attrs.getBoolean("keepThrowingExceptions");
        boolean enableLogRequestBody = attrs.getBoolean("enableLogRequestBody");
        boolean enableLogThrowingMethodParameters = attrs.getBoolean("enableLogThrowingMethodParameters");

        registerPropertiesBean(registry, strategies, excludedExceptions, httpResponseErrorMessage, logErrorMessage, keepThrowingExceptions, enableLogRequestBody, enableLogThrowingMethodParameters);
        registerLoggingStrategiesBeans(registry, strategies);
        registerRequestCachingFilter(registry, enableLogRequestBody);
        registerMethodParametersEnricherAdvice(registry, enableLogThrowingMethodParameters);
    }

    private Class<? extends UncaughtGuardLoggingStrategy>[] getLoggingStrategies(AnnotationAttributes attrs) {
        Class<? extends UncaughtGuardLoggingStrategy>[] strategies = (Class<? extends UncaughtGuardLoggingStrategy>[]) attrs.getClassArray("loggingStrategies");
        if (strategies.length == 0) {
            logger.fine("Retrieved empty logging strategies property, setting to default " + UncaughtGuardSystemErrorLoggingStrategy.class.getSimpleName());
            return new Class[]{UncaughtGuardSystemErrorLoggingStrategy.class};
        }
        return strategies;
    }

    private void registerPropertiesBean(
            BeanDefinitionRegistry registry,
            Class<? extends UncaughtGuardLoggingStrategy>[] strategies,
            Class<? extends RuntimeException>[] excludedExceptions,
            String httpResponseErrorMessage,
            String logErrorMessage,
            boolean keepThrowingExceptions,
            boolean enableLogRequestBody,
            boolean enableLogThrowingMethodParameters
    ) {
        RootBeanDefinition def = new RootBeanDefinition(UncaughtGuardProperties.class);
        def.getPropertyValues().add("loggingStrategies", strategies);
        def.getPropertyValues().add("excludedExceptions", excludedExceptions);
        def.getPropertyValues().add("httpResponseErrorMessage", httpResponseErrorMessage);
        def.getPropertyValues().add("logErrorMessage", logErrorMessage);
        def.getPropertyValues().add("keepThrowingExceptions", keepThrowingExceptions);
        def.getPropertyValues().add("enableLogRequestBody", enableLogRequestBody);
        registry.registerBeanDefinition("uncaughtGuardProperties", def);

        logger.fine(String.format(
                "Successfully registered UncaughtGuard properties.\n" +
                        "Registered properties:\n\n" +
                        "loggingStrategies                 : %s\n" +
                        "excludedExceptions                : %s\n" +
                        "httpResponseErrorMessage          : %s\n" +
                        "logErrorMessage                   : %s\n" +
                        "keepThrowingExceptions            : %s\n" +
                        "enableLogRequestBody              : %s\n" +
                        "enableLogThrowingMethodParameters : %s",
                Arrays.stream(strategies).map(Class::getSimpleName).collect(Collectors.joining(",")),
                Arrays.stream(excludedExceptions).map(Class::getSimpleName).collect(Collectors.joining(",")),
                httpResponseErrorMessage,
                logErrorMessage,
                keepThrowingExceptions,
                enableLogRequestBody,
                enableLogThrowingMethodParameters
        ));
    }

    private void registerLoggingStrategiesBeans(BeanDefinitionRegistry registry, Class<? extends UncaughtGuardLoggingStrategy>[] strategies) {
        for (Class<? extends UncaughtGuardLoggingStrategy> strategyClass : strategies) {
            // if class is an abstract class, throw exception
            if (java.lang.reflect.Modifier.isAbstract(strategyClass.getModifiers())) {
                throw new IllegalArgumentException(
                        "Specified logging strategy class " + strategyClass.getName() +
                                " is abstract and cannot be instantiated. Please provide a concrete implementation."
                );
            }

            // register the logging strategy as a bean definition
            RootBeanDefinition beanDef = new RootBeanDefinition(strategyClass);
            String beanName = decapitalize(strategyClass.getSimpleName());
            registry.registerBeanDefinition(beanName, beanDef);

            logger.fine(String.format("Successfully registered the specified logging strategy: %s", strategyClass.getName()));
        }

        // if the default system error logging strategy is not specified, register it anyways since it is used as a fallback
        if (Arrays.stream(strategies).noneMatch(strategy -> strategy.equals(UncaughtGuardSystemErrorLoggingStrategy.class))) {
            RootBeanDefinition beanDef = new RootBeanDefinition(UncaughtGuardSystemErrorLoggingStrategy.class);
            registry.registerBeanDefinition("uncaughtGuardSystemErrorLoggingStrategy", beanDef);
            logger.fine("Registered default logging strategy: " + UncaughtGuardSystemErrorLoggingStrategy.class.getName());
        }
    }

    private void registerRequestCachingFilter(BeanDefinitionRegistry registry, boolean enableLogRequestBody) {
        if (!enableLogRequestBody) {
            logger.fine("Request body logging is disabled, skipping request content caching filter registration.");
            return;
        }

        RootBeanDefinition beanDef = new RootBeanDefinition(UncaughtGuardContentRequestCachingFilter.class);
        registry.registerBeanDefinition("uncaughtGuardContentRequestCachingFilter", beanDef);

        logger.fine("Successfully enabled request body logging, by registering the request content caching filter");
    }

    private void registerMethodParametersEnricherAdvice(BeanDefinitionRegistry registry, boolean enableLogThrowingMethodParameters) {
        if (!enableLogThrowingMethodParameters) {
            logger.fine("Method parameters logging is disabled, skipping method parameters enricher advice registration.");
            return;
        }

        RootBeanDefinition beanDef = new RootBeanDefinition(UncaughtGuardMethodParametersEnricherAdvice.class);
        registry.registerBeanDefinition("uncaughtGuardMethodParametersEnricherAdvice", beanDef);

        logger.fine("Successfully enabled method parameters logging, by registering the method parameters enricher advice");
    }

    private String decapitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
