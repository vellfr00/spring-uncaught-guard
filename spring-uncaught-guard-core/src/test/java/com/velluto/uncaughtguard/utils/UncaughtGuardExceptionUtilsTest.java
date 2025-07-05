package com.velluto.uncaughtguard.utils;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardExceptionUtilsTest {
    private UncaughtGuardExceptionUtils utils;
    private UncaughtGuardProperties properties;

    @BeforeEach
    void setUp() {
        properties = mock(UncaughtGuardProperties.class);
        utils = new UncaughtGuardExceptionUtils();
        // inject mock properties via reflection
        try {
            var propField = UncaughtGuardExceptionUtils.class.getDeclaredField("properties");
            propField.setAccessible(true);
            propField.set(utils, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class CustomException extends RuntimeException {}
    static class AnotherException extends RuntimeException {}

    @Test
    void testIsExceptionExcluded_returnsTrueIfExcluded() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{CustomException.class});
        boolean result = utils.isExceptionExcluded(new CustomException());
        assertTrue(result);
    }

    @Test
    void testIsExceptionExcluded_returnsFalseIfNotExcluded() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{CustomException.class});
        boolean result = utils.isExceptionExcluded(new AnotherException());
        assertFalse(result);
    }

    @Test
    void testIsExceptionExcluded_emptyArrayReturnsFalse() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{});
        boolean result = utils.isExceptionExcluded(new CustomException());
        assertFalse(result);
    }

    @Test
    void testIsExceptionExcluded_nullArrayReturnsNPE() {
        when(properties.getExcludedExceptions()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> utils.isExceptionExcluded(new CustomException()));
    }
}

