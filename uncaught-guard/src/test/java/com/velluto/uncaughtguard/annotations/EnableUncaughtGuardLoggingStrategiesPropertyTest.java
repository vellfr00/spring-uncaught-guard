package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSlf4jLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class EnableUncaughtGuardLoggingStrategiesPropertyTest {
    @Nested
    @SpringBootTest
    @ContextConfiguration(classes = EnableUncaughtGuardSpecifiedLoggingStrategiesTest.SpecifiedLoggingStrategiesTestConfiguration.class)
    class EnableUncaughtGuardSpecifiedLoggingStrategiesTest {
        @EnableUncaughtGuard(loggingStrategies = {UncaughtGuardSystemErrorLoggingStrategy.class, UncaughtGuardSlf4jLoggingStrategy.class})
        static class SpecifiedLoggingStrategiesTestConfiguration {
            // This class is used to test the EnableUncaughtGuard annotation with custom logging strategies
            // It can be empty as the annotation itself is being tested
        }

        @Autowired
        private ApplicationContext applicationContext;

        /**
         * Test that, after applying the @EnableUncaughtGuard annotation with specified logging strategies,
         * the UncaughtGuardProperties bean is registered with the correct logging strategies.
         */
        @Test
        public void testEnableUncaughtGuardAnnotation_withSpecifiedLoggingStrategies() {
            UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);

            assertNotNull(properties.getLoggingStrategies());
            assertEquals(2, properties.getLoggingStrategies().length);
            assertTrue(Arrays.asList(properties.getLoggingStrategies()).contains(UncaughtGuardSystemErrorLoggingStrategy.class));
            assertTrue(Arrays.asList(properties.getLoggingStrategies()).contains(UncaughtGuardSlf4jLoggingStrategy.class));
        }
    }

    @Nested
    @SpringBootTest
    @ContextConfiguration(classes = EnableUncaughtGuardNoLoggingStrategiesTest.NoLoggingStrategiesTestConfiguration.class)
    class EnableUncaughtGuardNoLoggingStrategiesTest {
        @EnableUncaughtGuard(loggingStrategies = {})
        static class NoLoggingStrategiesTestConfiguration {
            // This class is used to test the EnableUncaughtGuard annotation without specifying logging strategies
            // It can be empty as the annotation itself is being tested
        }

        @Autowired
        private ApplicationContext applicationContext;

        /**
         * Test that, after applying the @EnableUncaughtGuard annotation without specifying logging strategies,
         * the UncaughtGuardProperties bean is registered with the default logging strategy.
         */
        @Test
        public void testEnableUncaughtGuardAnnotation_withoutSpecifiedLoggingStrategies() {
            UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);

            assertNotNull(properties.getLoggingStrategies());
            assertEquals(1, properties.getLoggingStrategies().length);
            assertEquals(UncaughtGuardSystemErrorLoggingStrategy.class, properties.getLoggingStrategies()[0]);
        }
    }
}
