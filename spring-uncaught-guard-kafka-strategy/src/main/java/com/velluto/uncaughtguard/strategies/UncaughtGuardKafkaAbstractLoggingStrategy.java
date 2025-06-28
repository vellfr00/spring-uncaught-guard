package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This abstract class provides a base implementation for logging uncaught exceptions to a Kafka topic.
 * Developers must extend this class and implement the methods to provide the Kafka bootstrap servers
 * and topic name.
 * It initializes the Kafka producer template and handles the logging of uncaught exceptions.
 */
public abstract class UncaughtGuardKafkaAbstractLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private static final Logger logger = Logger.getLogger(UncaughtGuardKafkaAbstractLoggingStrategy.class.getName());

    private List<String> kafkaBootstrapServers;
    private String kafkaTopicName;
    private KafkaTemplate<UUID, UncaughtGuardExceptionTrace> kafkaProducerTemplate;

    /**
     * Developers must implement this method to provide the list of Kafka bootstrap servers.
     * These will be used to connect to the Kafka cluster for logging uncaught exceptions.
     *
     * @return List of Kafka bootstrap servers
     */
    public abstract List<String> kafkaBootstrapServers();

    /**
     * Developers must implement this method to provide the Kafka topic name.
     * This topic will be used to publish the uncaught exception traces.
     *
     * @return Name of the Kafka topic
     */
    public abstract String kafkaTopicName();

    /**
     * This method is called after the bean is constructed and instantiated.
     * It is used to initialize the Kafka logging strategy by loading and validating the configuration,
     * and building the producer factory.
     * It ensures that the necessary Kafka configurations are set up before any logging occurs.
     *
     * @throws IllegalArgumentException if the Kafka bootstrap servers or topic name are not provided
     */
    @PostConstruct
    public final void init() {
        logger.fine("Initializing Kafka logging strategy for UncaughtGuard.");

        loadAndValidateConfiguration();
        buildKafkaProducerTemplate();

        logger.fine("Kafka logging strategy initialized successfully with bootstrap servers: " + kafkaBootstrapServers + " and topic name: " + kafkaTopicName);
    }

    /**
     * This method is called to load and validate the configuration for Kafka logging.
     * It retrieves the Kafka bootstrap servers and topic name from the abstract methods
     * and checks if they are not null or empty.
     * If any of the required configurations are missing,
     * an IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if the Kafka bootstrap servers or topic name are not provided
     */
    private void loadAndValidateConfiguration() {
        this.kafkaBootstrapServers = kafkaBootstrapServers();
        if (kafkaBootstrapServers == null || kafkaBootstrapServers.isEmpty())
            throw new IllegalArgumentException("Kafka bootstrap servers must be provided, please implement the kafkaBootstrapServers() method correctly.");

        this.kafkaTopicName = kafkaTopicName();
        if (kafkaTopicName == null || kafkaTopicName.isEmpty())
            throw new IllegalArgumentException("Kafka topic name must be provided, please implement the kafkaTopicName() method correctly.");
    }

    /**
     * This method builds the Kafka producer template using the provided bootstrap servers and topic name.
     * It creates a configuration map with the necessary properties for the Kafka producer,
     * including the bootstrap servers, key serializer, and value serializer.
     * It then creates a ProducerFactory and a KafkaTemplate
     * to be used for sending messages to the Kafka topic.
     */
    private void buildKafkaProducerTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaBootstrapServers));
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                UUIDSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);

        ProducerFactory<UUID, UncaughtGuardExceptionTrace> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        this.kafkaProducerTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Override
    protected final void log(UncaughtGuardExceptionTrace exceptionTrace) {
        this.kafkaProducerTemplate.send(this.kafkaTopicName, exceptionTrace.getTraceId(), exceptionTrace).join();
    }
}
