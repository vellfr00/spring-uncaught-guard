package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.filters.UncaughtGuardContentRequestCachingFilter;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

public class EnableUncaughtGuardEnableLogRequestBodyPropertyTest {
    @Nested
    @SpringBootTest
    @ContextConfiguration(classes = EnableUncaughtGuardEnableLogRequestBodyTest.EnableLogRequestBodyTestConfiguration.class)
    class EnableUncaughtGuardEnableLogRequestBodyTest {
        @EnableUncaughtGuard(enableLogRequestBody = false)
        static class EnableLogRequestBodyTestConfiguration {}

        @Autowired
        private ApplicationContext applicationContext;

        @Test
        void testEnableLogRequestBodyProperty() {
            UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
            assertFalse(properties.isEnableLogRequestBody());

            // Check that the filter is not registered
            assertThrows(NoSuchBeanDefinitionException.class, () -> {
                applicationContext.getBean(UncaughtGuardContentRequestCachingFilter.class);
            });
        }
    }

    @Nested
    @SpringBootTest
    @ContextConfiguration(classes = EnableUncaughtGuardEnableLogRequestBodyTrueTest.EnableLogRequestBodyTrueTestConfiguration.class)
    class EnableUncaughtGuardEnableLogRequestBodyTrueTest {
        @EnableUncaughtGuard(enableLogRequestBody = true)
        static class EnableLogRequestBodyTrueTestConfiguration {}

        @Autowired
        private ApplicationContext applicationContext;

        @Test
        void testEnableLogRequestBodyTrueProperty() {
            UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
            assertTrue(properties.isEnableLogRequestBody());

            // Check that the filter is registered
            assertNotNull(applicationContext.getBean(UncaughtGuardContentRequestCachingFilter.class));
        }
    }
}

