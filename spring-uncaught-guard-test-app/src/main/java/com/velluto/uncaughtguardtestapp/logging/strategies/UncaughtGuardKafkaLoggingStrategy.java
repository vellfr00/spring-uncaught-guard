package com.velluto.uncaughtguardtestapp.logging.strategies;

import com.velluto.uncaughtguard.strategies.UncaughtGuardKafkaAbstractLoggingStrategy;

import java.util.List;

public class UncaughtGuardKafkaLoggingStrategy extends UncaughtGuardKafkaAbstractLoggingStrategy {
    @Override
    public List<String> kafkaBootstrapServers() {
        return List.of("localhost:9092");
    }

    @Override
    public String kafkaTopicName() {
        return "spring-uncaught-guard-test-app-incidents";
    }
}
