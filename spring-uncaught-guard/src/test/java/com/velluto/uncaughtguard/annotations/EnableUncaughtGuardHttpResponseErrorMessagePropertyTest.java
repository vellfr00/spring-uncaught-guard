package com.velluto.uncaughtguard.annotations;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = EnableUncaughtGuardHttpResponseErrorMessagePropertyTest.HttpResponseErrorMessageTestConfiguration.class)
@EnableUncaughtGuard(httpResponseErrorMessage = "Custom error message")
public class EnableUncaughtGuardHttpResponseErrorMessagePropertyTest {
    static class HttpResponseErrorMessageTestConfiguration {}

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testHttpResponseErrorMessageProperty() {
        UncaughtGuardProperties properties = applicationContext.getBean(UncaughtGuardProperties.class);
        assertEquals("Custom error message", properties.getHttpResponseErrorMessage());
    }
}
