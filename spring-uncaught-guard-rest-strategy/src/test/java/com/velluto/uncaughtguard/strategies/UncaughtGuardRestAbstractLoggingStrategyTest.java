package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UncaughtGuardRestAbstractLoggingStrategyTest {
    private UncaughtGuardRestAbstractLoggingStrategy strategy;
    private UncaughtGuardExceptionTrace trace;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);
        restTemplate = mock(RestTemplate.class);
        // Mock the postForEntity method to return null (since it's not used)
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(null);
        strategy = new UncaughtGuardRestAbstractLoggingStrategy() {
            @Override
            public String restEndpoint() {
                return "http://localhost:8080/log";
            }
        };
        // Reflection to inject mock RestTemplate
        try {
            var field = UncaughtGuardRestAbstractLoggingStrategy.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(strategy, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLoadAndValidateRestEndpoint_valid() {
        assertEquals("http://localhost:8080/log", strategy.restEndpoint());
    }

    @Test
    void testLog_callsPostForEntityOnce() {
        // use reflection to access the private method and call loadAndValidateRestEndpoint
        try {
            var m = UncaughtGuardRestAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateRestEndpoint");
            m.setAccessible(true);
            m.invoke(strategy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        strategy.log(trace);
        verify(restTemplate, times(1)).postForEntity(eq("http://localhost:8080/log"), eq(trace), eq(Void.class));
    }

    @Test
    void testLoadAndValidateRestEndpoint_nullEndpoint_throwsException() {
        UncaughtGuardRestAbstractLoggingStrategy nullEndpointStrategy = new UncaughtGuardRestAbstractLoggingStrategy() {
            @Override
            public String restEndpoint() {
                return null;
            }
        };
        Exception ex = assertThrows(InvocationTargetException.class, () -> {
            // Reflection to call private method
            var m = UncaughtGuardRestAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateRestEndpoint");
            m.setAccessible(true);
            m.invoke(nullEndpointStrategy);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        IllegalArgumentException cause = (IllegalArgumentException) ex.getCause();
        assertTrue(cause.getMessage().contains("Rest endpoint must not be null or empty"));
    }

    @Test
    void testLoadAndValidateRestEndpoint_emptyEndpoint_throwsException() {
        UncaughtGuardRestAbstractLoggingStrategy emptyEndpointStrategy = new UncaughtGuardRestAbstractLoggingStrategy() {
            @Override
            public String restEndpoint() {
                return "";
            }
        };
        Exception ex = assertThrows(InvocationTargetException.class, () -> {
            // Reflection to call private method
            var m = UncaughtGuardRestAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateRestEndpoint");
            m.setAccessible(true);
            m.invoke(emptyEndpointStrategy);
        });
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
        IllegalArgumentException cause = (IllegalArgumentException) ex.getCause();
        assertTrue(cause.getMessage().contains("Rest endpoint must not be null or empty"));
    }
}
