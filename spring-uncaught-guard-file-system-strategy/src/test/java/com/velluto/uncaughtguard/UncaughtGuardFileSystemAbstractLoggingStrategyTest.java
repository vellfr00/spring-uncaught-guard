package com.velluto.uncaughtguard;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.strategies.UncaughtGuardFileSystemAbstractLoggingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UncaughtGuardFileSystemAbstractLoggingStrategyTest {
    @TempDir
    Path tempDir;
    private UncaughtGuardFileSystemAbstractLoggingStrategy strategy;
    private UncaughtGuardExceptionTrace trace;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);
        when(trace.getTraceId()).thenReturn(UUID.randomUUID());
        when(trace.getIncidentTimestamp()).thenReturn(LocalDateTime.now());
        when(trace.getMethod()).thenReturn("GET");
        when(trace.getPath()).thenReturn("/api/test");
        when(trace.getQueryParams()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getHeaders()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getBody()).thenReturn("body");
        when(trace.getJSONSerializedThrowingMethods()).thenReturn("[]");
        when(trace.getLoggableExceptionStackTrace()).thenReturn("stacktrace");
        strategy = new UncaughtGuardFileSystemAbstractLoggingStrategy() {
            @Override
            public String filePath() {
                return tempDir.toString();
            }

            @Override
            protected String getLogErrorMessage() {
                return "Error";
            }
        };
    }

    @Test
    void testLog_callsWriteStringOnce() throws Exception {
        // use reflection to access the private method and call loadAndValidateFilePath
        try {
            var m = UncaughtGuardFileSystemAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateFilePath");
            m.setAccessible(true);
            m.invoke(strategy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any())).thenReturn(null);
            filesMock.when(() -> Files.writeString(any(), anyString())).thenReturn(null);

            try {
                var logMethod = UncaughtGuardFileSystemAbstractLoggingStrategy.class.getDeclaredMethod("log", UncaughtGuardExceptionTrace.class);
                logMethod.setAccessible(true);
                logMethod.invoke(strategy, trace);
            } catch (InvocationTargetException e) {
                throw e;
            }

            Path expectedPath = Path.of(tempDir.toString(), trace.getTraceId() + ".log");
            filesMock.verify(() -> Files.writeString(eq(expectedPath), anyString()), times(1));
        }
    }

    @Test
    void testLoadAndValidateFilePath_valid() {
        assertEquals(tempDir.toString(), strategy.filePath());
    }

    @Test
    void testLoadAndValidateFilePath_nullPath_throwsException() {
        UncaughtGuardFileSystemAbstractLoggingStrategy nullPathStrategy = new UncaughtGuardFileSystemAbstractLoggingStrategy() {
            @Override
            public String filePath() {
                return null;
            }

            @Override
            protected String getLogErrorMessage() {
                return "Error";
            }
        };
        Exception ex = assertThrows(InvocationTargetException.class, () -> {
            var m = UncaughtGuardFileSystemAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateFilePath");
            m.setAccessible(true);
            m.invoke(nullPathStrategy);
        });
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        IllegalArgumentException cause = (IllegalArgumentException) ex.getCause();
        assertTrue(cause.getMessage().contains("File path must not be null or empty"));
    }

    @Test
    void testLoadAndValidateFilePath_emptyPath_throwsException() {
        UncaughtGuardFileSystemAbstractLoggingStrategy emptyPathStrategy = new UncaughtGuardFileSystemAbstractLoggingStrategy() {
            @Override
            public String filePath() {
                return "";
            }

            @Override
            protected String getLogErrorMessage() {
                return "Error";
            }
        };
        Exception ex = assertThrows(InvocationTargetException.class, () -> {
            var m = UncaughtGuardFileSystemAbstractLoggingStrategy.class.getDeclaredMethod("loadAndValidateFilePath");
            m.setAccessible(true);
            m.invoke(emptyPathStrategy);
        });
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        IllegalArgumentException cause = (IllegalArgumentException) ex.getCause();
        assertTrue(cause.getMessage().contains("File path must not be null or empty"));
    }
}
