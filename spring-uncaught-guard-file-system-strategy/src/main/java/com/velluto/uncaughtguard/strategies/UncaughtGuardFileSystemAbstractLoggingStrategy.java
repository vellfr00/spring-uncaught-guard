package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.exceptions.UncaughtGuardMethodParametersEnrichedRuntimeException;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Abstract strategy for logging uncaught exceptions to the file system.
 * It saves exception traces to files in a specified directory, into a file named with the trace ID.
 * Developers must extend this class and implement the filePath() method to provide the directory path
 * where the log files will be stored.
 * The file path must be valid, not null nor empty.
 */
public abstract class UncaughtGuardFileSystemAbstractLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private final Logger logger = Logger.getLogger(UncaughtGuardFileSystemAbstractLoggingStrategy.class.getName());

    private String filePath;

    public abstract String filePath();

    /**
     * This method is called after the bean is constructed and instantiated.
     * It is used to initialize the file system logging strategy by loading and validating the file path.
     * It ensures that the necessary file path configuration is set up before any logging occurs.
     */
    @PostConstruct
    public final void init() {
        logger.fine("Initializing File System Logging Strategy");

        loadAndValidateFilePath();

        logger.fine("File System Logging Strategy initialized with file path: " + filePath);
    }

    /**
     * Load the file path from the value passed from the filePath() method.
     * This method validates that the file path is not null or empty.
     * If the file path is invalid, it throws an IllegalArgumentException.
     *
     * @throws IllegalArgumentException if the file path is null or empty
     */
    private void loadAndValidateFilePath() {
        String filePath = filePath();
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("File path must not be null or empty");

        this.filePath = filePath;
    }

    private String getLoggableExceptionStackTraceString(RuntimeException exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private String getThrowingMethodsLoggableString(RuntimeException exception) {
        if (!(exception instanceof UncaughtGuardMethodParametersEnrichedRuntimeException enrichedRuntimeException))
            return "";

        return enrichedRuntimeException.getJSONSerializedThrowingMethods();
    }

    private String getLoggableExceptionTraceString(UncaughtGuardExceptionTrace exceptionTrace) {
        StringBuilder sb = new StringBuilder();

        sb.append(getLogErrorMessage()).append('\n').append('\n');
        sb.append("Trace ID     : ").append(exceptionTrace.getTraceId()).append('\n');
        sb.append("Timestamp    : ").append(exceptionTrace.getIncidentTimestamp()).append('\n');
        sb.append("Method       : ").append(exceptionTrace.getMethod()).append('\n');
        sb.append("Path         : ").append(exceptionTrace.getPath()).append('\n');
        sb.append("Query Params : ").append(exceptionTrace.getQueryParams().toString()).append('\n');
        sb.append("Headers      : ").append(exceptionTrace.getHeaders().toString()).append('\n');
        sb.append("Body         : ").append('\n').append(exceptionTrace.getBody()).append('\n');
        sb.append("Methods      : ").append('\n').append(getThrowingMethodsLoggableString(exceptionTrace.getException())).append('\n');
        sb.append("Exception    : ").append('\n').append(getLoggableExceptionStackTraceString(exceptionTrace.getException()));

        return sb.toString();
    }

    @Override
    protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
        UUID traceId = exceptionTrace.getTraceId();

        try {
            // create a file with the trace ID as the name in the specified file path
            Path logFilePath = Path.of(filePath, traceId + ".log");
            Files.createDirectories(logFilePath.getParent());
            Files.writeString(logFilePath,getLoggableExceptionTraceString(exceptionTrace));
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while logging using File System Strategy exception with TraceID: " + traceId, e);
        }
    }
}
