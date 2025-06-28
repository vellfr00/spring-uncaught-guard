package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = EnableUncaughtGuardExcludedExceptionsPropertyTest.ExcludedExceptionsTestConfiguration.class)
@EnableUncaughtGuard(excludedExceptions = {IllegalArgumentException.class, NullPointerException.class})
public class EnableUncaughtGuardExcludedExceptionsPropertyTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testExcludedExceptionsProperty() {
        UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
        assertNotNull(properties.getExcludedExceptions());
        assertEquals(2, properties.getExcludedExceptions().length);
        assertTrue(Arrays.asList(properties.getExcludedExceptions()).contains(IllegalArgumentException.class));
        assertTrue(Arrays.asList(properties.getExcludedExceptions()).contains(NullPointerException.class));
    }

    static class ExcludedExceptionsTestConfiguration {
    }
}
