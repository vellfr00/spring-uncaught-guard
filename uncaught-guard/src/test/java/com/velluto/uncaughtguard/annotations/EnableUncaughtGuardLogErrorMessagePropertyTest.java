package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = EnableUncaughtGuardLogErrorMessagePropertyTest.LogErrorMessageTestConfiguration.class)
@EnableUncaughtGuard(logErrorMessage = "Custom log message")
public class EnableUncaughtGuardLogErrorMessagePropertyTest {
    static class LogErrorMessageTestConfiguration {}

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testLogErrorMessageProperty() {
        UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
        assertEquals("Custom log message", properties.getLogErrorMessage());
    }
}
