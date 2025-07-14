package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.exceptions.UncaughtGuardMethodParametersEnrichedRuntimeException;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.utils.UncaughtGuardExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UncaughtGuardMethodParametersEnricherAdviceTest {
    private UncaughtGuardMethodParametersEnricherAdvice advice;
    private UncaughtGuardProperties properties;
    private UncaughtGuardExceptionUtils exceptionUtils;
    private JoinPoint joinPoint;
    private Signature methodSignature;

    @BeforeEach
    void setUp() throws Exception {
        advice = new UncaughtGuardMethodParametersEnricherAdvice();
        properties = mock(UncaughtGuardProperties.class);
        exceptionUtils = mock(UncaughtGuardExceptionUtils.class);
        joinPoint = mock(JoinPoint.class);
        when(joinPoint.getTarget()).thenReturn(Mockito.mock(Object.class));
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.getThis()).thenReturn(Mockito.mock(Object.class));
        methodSignature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toString()).thenReturn("testMethod()");

        var propField = UncaughtGuardMethodParametersEnricherAdvice.class.getDeclaredField("properties");
        propField.setAccessible(true);
        propField.set(advice, properties);
        var utilsField = UncaughtGuardMethodParametersEnricherAdvice.class.getDeclaredField("exceptionUtils");
        utilsField.setAccessible(true);
        utilsField.set(advice, exceptionUtils);
    }

    @Test
    void testCaptureMethodParameters_NotRuntimeException() {
        Throwable throwable = new Exception("Checked exception");
        // it should not throw an exception
        advice.captureMethodParameters(joinPoint, throwable);
    }

    @Test
    void testCaptureMethodParameters_ExceptionExcluded() {
        RuntimeException runtimeException = new RuntimeException("Test");
        when(exceptionUtils.isExceptionExcluded(runtimeException)).thenReturn(true);
        // it should not throw an exception
        advice.captureMethodParameters(joinPoint, runtimeException);
    }

    @Test
    void testCaptureMethodParameters_EnrichesAndThrows() {
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 2});
        NullPointerException runtimeException = new NullPointerException("Test");
        when(exceptionUtils.isExceptionExcluded(runtimeException)).thenReturn(false);
        UncaughtGuardMethodParametersEnrichedRuntimeException thrown = assertThrows(
                UncaughtGuardMethodParametersEnrichedRuntimeException.class,
                () -> advice.captureMethodParameters(joinPoint, runtimeException)
        );
        assertEquals(runtimeException, thrown.getOriginalExceptionReference());
        assertEquals(1, thrown.getThrowingMethods().size());
        assertEquals("testMethod()", thrown.getThrowingMethods().get(0).getMethodSignature());
        assertEquals(2, thrown.getThrowingMethods().get(0).getPassedParameters().length);
        assertEquals("arg1", thrown.getThrowingMethods().get(0).getPassedParameters()[0].getValue());
        assertEquals(2, thrown.getThrowingMethods().get(0).getPassedParameters()[1].getValue());
    }
}

