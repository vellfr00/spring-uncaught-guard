package com.velluto.uncaughtguard.registrars;

import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UncaughtGuardRegistrarTest {
    @Mock
    private BeanDefinitionRegistry registry;

    private UncaughtGuardRegistrar registrar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrar = new UncaughtGuardRegistrar();
    }

    @Test
    void testGetLoggingStrategiesReturnsDefaultIfEmpty() throws Exception {
        AnnotationAttributes attrs = new AnnotationAttributes();
        attrs.put("loggingStrategies", new Class[]{});
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("getLoggingStrategies", AnnotationAttributes.class);
        method.setAccessible(true);
        Class[] result = (Class[]) method.invoke(registrar, attrs);
        assertEquals(1, result.length);
        assertEquals(UncaughtGuardSystemErrorLoggingStrategy.class, result[0]);
    }

    @Test
    void testGetLoggingStrategiesReturnsProvided() throws Exception {
        AnnotationAttributes attrs = new AnnotationAttributes();
        attrs.put("loggingStrategies", new Class[]{UncaughtGuardSystemErrorLoggingStrategy.class});
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("getLoggingStrategies", AnnotationAttributes.class);
        method.setAccessible(true);
        Class[] result = (Class[]) method.invoke(registrar, attrs);
        assertEquals(1, result.length);
        assertEquals(UncaughtGuardSystemErrorLoggingStrategy.class, result[0]);
    }

    @Test
    void testRegisterPropertiesBeanRegistersBean() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod(
                "registerPropertiesBean",
                BeanDefinitionRegistry.class,
                Class[].class,
                Class[].class,
                String.class,
                String.class,
                boolean.class,
                boolean.class,
                boolean.class
        );
        method.setAccessible(true);
        Class[] strategies = new Class[]{UncaughtGuardSystemErrorLoggingStrategy.class};
        Class[] excluded = new Class[]{};
        method.invoke(registrar, registry, strategies, excluded, "err", "log", true, false, false);
        verify(registry).registerBeanDefinition(eq("uncaughtGuardProperties"), any(RootBeanDefinition.class));
    }

    @Test
    void testRegisterLoggingStrategiesBeansRegistersEachStrategy() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("registerLoggingStrategiesBeans", BeanDefinitionRegistry.class, Class[].class);
        method.setAccessible(true);
        Class[] strategies = new Class[]{UncaughtGuardSystemErrorLoggingStrategy.class};
        method.invoke(registrar, registry, strategies);
        verify(registry).registerBeanDefinition(eq("uncaughtGuardSystemErrorLoggingStrategy"), any(RootBeanDefinition.class));
    }

    @Test
    void testRegisterLoggingStrategiesBeansRegistersDefaultIfNotSpecified() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("registerLoggingStrategiesBeans", BeanDefinitionRegistry.class, Class[].class);
        method.setAccessible(true);
        Class[] strategies = new Class[]{};
        method.invoke(registrar, registry, strategies);
        verify(registry).registerBeanDefinition(eq("uncaughtGuardSystemErrorLoggingStrategy"), any(RootBeanDefinition.class));
    }

    @Test
    void testRegisterRequestCachingFilterRegistersIfEnabled() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("registerRequestCachingFilter", BeanDefinitionRegistry.class, boolean.class);
        method.setAccessible(true);
        method.invoke(registrar, registry, true);
        verify(registry).registerBeanDefinition(eq("uncaughtGuardContentRequestCachingFilter"), any(RootBeanDefinition.class));
    }

    @Test
    void testRegisterRequestCachingFilterSkipsIfDisabled() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("registerRequestCachingFilter", BeanDefinitionRegistry.class, boolean.class);
        method.setAccessible(true);
        method.invoke(registrar, registry, false);
        verify(registry, never()).registerBeanDefinition(eq("uncaughtGuardContentRequestCachingFilter"), any(RootBeanDefinition.class));
    }

    @Test
    void testDecapitalize() throws Exception {
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("decapitalize", String.class);
        method.setAccessible(true);
        assertEquals("foo", method.invoke(registrar, "Foo"));
        assertEquals("bar", method.invoke(registrar, "bar"));
        assertEquals("", method.invoke(registrar, ""));
        assertNull(method.invoke(registrar, (Object) null));
    }

    @Test
    void testRegisterLoggingStrategiesBeansThrowsIfAbstractClassSpecified() throws Exception {
        abstract class AbstractStrategy extends UncaughtGuardLoggingStrategy {
            // Left empty intentionally, for testing purposes it is not needed
        }
        Method method = UncaughtGuardRegistrar.class.getDeclaredMethod("registerLoggingStrategiesBeans", BeanDefinitionRegistry.class, Class[].class);
        method.setAccessible(true);
        Class[] strategies = new Class[]{AbstractStrategy.class};
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                method.invoke(registrar, registry, (Object) strategies)
        );
        Throwable cause = exception.getCause();
        assertInstanceOf(IllegalArgumentException.class, cause);
        assertTrue(cause.getMessage().contains("is abstract and cannot be instantiated"));
    }
}
