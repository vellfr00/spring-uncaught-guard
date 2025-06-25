package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.advices.UncaughtGuardRestControllerAdvice;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = EnableUncaughtGuardNoPropertiesTest.EnableUncaughtGuardTestConfiguration.class)
public class EnableUncaughtGuardNoPropertiesTest {
    @EnableUncaughtGuard
    static class EnableUncaughtGuardTestConfiguration {
        // This class is used to test the EnableUncaughtGuard annotation
        // It can be empty as the annotation itself is being tested
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Test that, after applying the @EnableUncaughtGuard annotation, the following beans are registered:
     * - UncaughtGuardRestControllerAdvice
     * - UncaughtGuardProperties
     * <p>
     * This is a basic test to ensure that the annotation works as expected
     * This test will fail if the beans are not registered correctly.
     */
    @Test
    public void testEnableUncaughtGuardAnnotation_necessaryBeansAreRegistered() {
        assertNotNull(applicationContext.getBean(UncaughtGuardRestControllerAdvice.class));
        assertNotNull(applicationContext.getBean(UncaughtGuardProperties.class));
    }

    /**
     * Test all the properties of the UncaughtGuardProperties bean to be set to their default values
     */
    @Test
    public void testUncaughtGuardProperties_defaultValues() {
        UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);

        assertNotNull(properties.getLoggingStrategies());
        assertNotNull(properties.getExcludedExceptions());
        assertNotNull(properties.getHttpResponseErrorMessage());
        assertNotNull(properties.getLogErrorMessage());

        assertEquals(1, properties.getLoggingStrategies().length);
        assertEquals(UncaughtGuardSystemErrorLoggingStrategy.class, properties.getLoggingStrategies()[0]);

        assertEquals("An unhandled exception has been caught", properties.getLogErrorMessage());
        assertEquals("Internal Server Error: an unexpected error occurred", properties.getHttpResponseErrorMessage());

        assertFalse(properties.isKeepThrowingExceptions());
        assertTrue(properties.isEnableLogRequestBody());

        assertEquals(0, properties.getExcludedExceptions().length);
    }
}
