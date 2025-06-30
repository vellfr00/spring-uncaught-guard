package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardKafkaAbstractLoggingStrategyTest {
    private Environment environment;
    private KafkaTemplate<UUID, UncaughtGuardExceptionTrace> kafkaTemplate;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        kafkaTemplate = mock(KafkaTemplate.class);
    }

    @Test
    void loadAndValidateKafkaBootstrapServers_fromMethod() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return List.of("localhost:9092"); }
            @Override public String kafkaTopicName() { return "topic"; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        strategy.init(); // should not throw
        assertEquals("localhost:9092", ReflectionTestUtils.getField(strategy, "kafkaBootstrapServers"));
        assertEquals("topic", ReflectionTestUtils.getField(strategy, "kafkaTopicName"));
    }

    @Test
    void loadAndValidateKafkaBootstrapServers_fromEnv() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return Collections.emptyList(); }
            @Override public String kafkaTopicName() { return "topic"; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        when(environment.getProperty("spring.kafka.bootstrap-servers")).thenReturn("envhost:9092,envhost2:9092");
        strategy.init(); // should not throw
        assertEquals("envhost:9092,envhost2:9092", ReflectionTestUtils.getField(strategy, "kafkaBootstrapServers"));
        assertEquals("topic", ReflectionTestUtils.getField(strategy, "kafkaTopicName"));
    }

    @Test
    void loadAndValidateKafkaBootstrapServers_missing_throws() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return Collections.emptyList(); }
            @Override public String kafkaTopicName() { return "topic"; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        when(environment.getProperty("spring.kafka.bootstrap-servers")).thenReturn("");
        assertThrows(IllegalArgumentException.class, strategy::init);
    }

    @Test
    void loadAndValidateKafkaTopicName_fromMethod() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return List.of("localhost:9092"); }
            @Override public String kafkaTopicName() { return "topic"; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        strategy.init(); // should not throw
    }

    @Test
    void loadAndValidateKafkaTopicName_missing_throws() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return List.of("localhost:9092"); }
            @Override public String kafkaTopicName() { return null; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        assertThrows(IllegalArgumentException.class, strategy::init);
    }

    @Test
    void log_callsSendOnce() {
        class Strategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
            @Override public List<String> kafkaBootstrapServers() { return List.of("localhost:9092"); }
            @Override public String kafkaTopicName() { return "topic"; }
        }
        Strategy strategy = new Strategy();
        ReflectionTestUtils.setField(strategy, "environment", environment);
        ReflectionTestUtils.setField(strategy, "kafkaProducerTemplate", kafkaTemplate);
        ReflectionTestUtils.setField(strategy, "kafkaTopicName", "topic");
        UncaughtGuardExceptionTrace trace = mock(UncaughtGuardExceptionTrace.class);
        when(trace.getTraceId()).thenReturn(UUID.randomUUID());
        when(kafkaTemplate.send(anyString(), any(UUID.class), any())).thenReturn(mock(java.util.concurrent.CompletableFuture.class));
        strategy.log(trace);
        verify(kafkaTemplate, times(1)).send(eq("topic"), any(UUID.class), eq(trace));
    }
}
