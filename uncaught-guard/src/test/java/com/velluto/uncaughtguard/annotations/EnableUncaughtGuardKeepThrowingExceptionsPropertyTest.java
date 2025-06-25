package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = EnableUncaughtGuardKeepThrowingExceptionsPropertyTest.KeepThrowingExceptionsTestConfiguration.class)
@EnableUncaughtGuard(keepThrowingExceptions = true)
public class EnableUncaughtGuardKeepThrowingExceptionsPropertyTest {
    static class KeepThrowingExceptionsTestConfiguration {}

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testKeepThrowingExceptionsProperty() {
        UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
        assertTrue(properties.isKeepThrowingExceptions());
    }
}
