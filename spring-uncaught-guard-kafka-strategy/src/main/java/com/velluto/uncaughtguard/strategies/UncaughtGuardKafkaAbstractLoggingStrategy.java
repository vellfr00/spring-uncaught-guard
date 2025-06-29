package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
 * It is possible to specify null or empty values for the bootstrap servers only, in order to load them from the environment variable `spring.kafka.bootstrap-servers`.
 * It initializes the Kafka producer template and handles the logging of uncaught exceptions.
 */
public abstract class UncaughtGuardKafkaAbstractLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private static final Logger logger = Logger.getLogger(UncaughtGuardKafkaAbstractLoggingStrategy.class.getName());

    @Autowired
    private Environment environment;

    private String kafkaBootstrapServers;
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
        logger.fine("Initializing Kafka logging strategy for Uncaught Guard.");

        loadAndValidateKafkaBootstrapServers();
        loadAndValidateKafkaTopicName();
        buildKafkaProducerTemplate();

        logger.fine("Successfully initialized Kafka logging strategy for Uncaught Guard with bootstrap servers: " + kafkaBootstrapServers + " and topic name: " + kafkaTopicName);
    }

    /**
     * This method loads and validates the Kafka bootstrap servers and topic name.
     * It retrieves the bootstrap servers from the abstract method
     * and checks if they are not null or empty.
     * If the bootstrap servers are not provided in the method, meaning it returns null or empty,
     * it attempts to load them from the environment variable `spring.kafka.bootstrap-servers`.
     * If the bootstrap servers are still not found,
     * an IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if the Kafka bootstrap servers are not provided both from the method and environment variable
     */
    private void loadAndValidateKafkaBootstrapServers() {
        List<String> bootstrapServersFromMethod = kafkaBootstrapServers();
        if (bootstrapServersFromMethod != null && !bootstrapServersFromMethod.isEmpty()) {
            this.kafkaBootstrapServers = String.join(",", bootstrapServersFromMethod);
            logger.fine("Retrieved Kafka bootstrap servers from method kafkaBootstrapServers: " + this.kafkaBootstrapServers);
            return;
        }

        logger.fine("Null or empty Kafka bootstrap servers provided from method kafkaBootstrapServers, trying to load from environment variable spring.kafka.bootstrap-servers");
        String bootstrapServersFromEnv = environment.getProperty("spring.kafka.bootstrap-servers");
        if (bootstrapServersFromEnv != null && !bootstrapServersFromEnv.isEmpty()) {
            this.kafkaBootstrapServers = bootstrapServersFromEnv;
            logger.fine("Retrieved Kafka bootstrap servers from environment variable spring.kafka.bootstrap-servers: " + this.kafkaBootstrapServers);
        } else {
            throw new IllegalArgumentException("Kafka bootstrap servers must be provided, please implement the kafkaBootstrapServers method or set the environment variable spring.kafka.bootstrap-servers.");
        }
    }

    /**
     * This method is called to load and validate the Kafka topic name.
     * It retrieves the topic name from the abstract method
     * and checks if it is not null or empty.
     * If the topic name is not provided,
     * an IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if the Kafka topic name is not provided
     */
    private void loadAndValidateKafkaTopicName() {
        String topicName = kafkaTopicName();
        if (topicName == null || topicName.isEmpty())
            throw new IllegalArgumentException("Kafka topic name must be provided, please implement the kafkaTopicName method correctly.");

        logger.fine("Retrieved Kafka topic name from method kafkaTopicName: " + topicName);
        this.kafkaTopicName = topicName;
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
                kafkaBootstrapServers);
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
