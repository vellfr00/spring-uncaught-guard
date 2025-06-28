# 🌱 Spring Uncaught Guard

The Guardian for uncaught exceptions in your Spring REST services.

## 📑 Table of Contents
- [📝 Overview](#-overview)
  - [🚀 Key Features](#-key-features)
  - [⚙️ How It Works](#-how-it-works)
- [🗂️ Project Structure](#-project-structure)
- [🛠️ Usage](#-usage)
  - [⚙️ Annotation Features](#-annotation-features)
- [🪵 Logging Strategies](#-logging-strategies)
  - [🏗️ Built-in Logging Strategies](#-built-in-logging-strategies)
    - [📦 System.err Logging Strategy](#-systemerr-logging-strategy)
    - [📦 Java Logger Logging Strategy](#-java-logger-logging-strategy)
    - [📦 SLF4J Logging Strategy](#-slf4j-logging-strategy)
    - [📦 Kafka Logging Strategy](#-kafka-logging-strategy)

# 📝 Overview

Spring Uncaught Guard is a powerful and easy-to-use library that empowers you to seamlessly intercept and log uncaught
exceptions in your Spring REST applications.
With a focus on simplicity and developer productivity, it enables you to build robust applications without the hassle of
manual exception management.

## 🚀 Key Features

- 🚨 **Automatic Exception Handling**: Effortlessly intercepts and logs uncaught runtime exceptions in your Spring REST
  applications, ensuring no error goes unnoticed.
- 📝 **Verbose Logging**: Delivers comprehensive logs for every exception, including stack traces and detailed request
  data such as headers, cookies, and body content.
- ⚡ **Zero Configuration Required**: Instantly operational with minimal setup—just add the `@EnableUncaughtGuard`
  annotation to your main application class and you’re ready to go.
- 🐞 **Debug-Friendly**: Assigns a unique identifier to each exception, which is returned in the error response, making
  it straightforward to trace and debug issues across distributed systems.
- 🛠️ **Highly Customizable**: Offers flexible customization options directly through the annotation, allowing you to
  adapt the library’s behavior to your specific needs.
- 🔌 **Easily Extensible**: Provides extension points for custom exception logging, other than the ones already provided,
  enabling seamless integration with your existing logging and monitoring infrastructure.

## ⚙️ How It Works

Spring Uncaught Guard utilizes Spring's `@RestControllerAdvice` mechanism to seamlessly intercept uncaught exceptions
thrown by your REST controllers. By default, it is configured to handle all exceptions that are subclasses of
`RuntimeException`, which are commonly used for application-specific errors.

If you have defined a custom `@RestControllerAdvice` for a specific exception type, your custom handler will take
precedence, and those exceptions will not be intercepted by this library. This ensures you retain full control and
flexibility over exception handling for known cases, while Spring Uncaught Guard acts as a safety net for truly
unexpected errors.

When an uncaught exception occurs, the library automatically captures comprehensive details—including the stack trace,
request headers, cookies, and body content—and assigns a unique trace identifier (`traceId`) to the event. These details
are then logged using your chosen logging strategies, and a standardized error response containing the `traceId` is
returned to the client, making debugging and error tracking straightforward.

# 🗂️ Project Structure

- 📦 `spring-uncaught-guard-core`: Contains the core library code, alongside with implementations of the `System.err`
  logging strategy and the `Java Logger` logging strategy, which are basic implementations and do not require any other dependencies.
- 📦 `spring-uncaught-guard-slf4j-strategy`: Contains the SLF4J logging strategy implementation, which requires SLF4J as
  a dependency.
- 📦 `spring-uncaught-guard-kafka-strategy`: Contains the Kafka logging strategy implementation, which
  requires Kafka as a dependency.
- 🧪 `spring-uncaught-guard-test-app`: A sample Spring Boot application that demonstrates the library in action and
  serves as a testing ground.

# 🛠️ Usage

First of all, add the dependency to your project. If you are using Maven, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Add then the `@EnableUncaughtGuard` annotation to your main Spring Boot application class to enable automatic uncaught
exception handling:

```java
@SpringBootApplication
@EnableUncaughtGuard
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

In the simplest case, this is all you need to do. No configuration files or additional setup required!
Just run your application, and it will automatically log uncaught exceptions in your REST controllers.

## ⚙️ Annotation Features

If you need more control over the behavior of the uncaught exception handling, you can customize it using the
`@EnableUncaughtGuard` annotation.
The `@EnableUncaughtGuard` annotation provides several customization options:

- 📝 **loggingStrategies**: Specify one or more logging strategies (implementations of `UncaughtGuardLoggingStrategy`).
  Default: logs to System.err. You can use built-in strategies or create your own custom logging strategy.
- 🚫 **excludedExceptions**: List exception types (subclasses of `RuntimeException`) to exclude from automatic handling.
- 💬 **httpResponseErrorMessage**: Customize the error message returned in the HTTP response.
- 🪵 **logErrorMessage**: Customize the error message that is logged.
- 🔁 **keepThrowingExceptions**: If true, rethrows the exception after handling (disables the custom HTTP response and
  traceId).
- 📦 **enableLogRequestBody**: If true (default), enables logging of the HTTP request body (may impact performance, but
  if not enabled you will miss request body logging).

Advanced example:

```java
@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {CustomLoggingStrategy.class},
        excludedExceptions = {IllegalArgumentException.class},
        httpResponseErrorMessage = "Custom internal error message",
        logErrorMessage = "Unhandled exception caught!",
        keepThrowingExceptions = false,
        enableLogRequestBody = true
)
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

# 🪵 Logging Strategies

You can use provided logging strategies or create your own custom logging strategy by extending the abstract
`UncaughtGuardLoggingStrategy` class and implementing the abstract `log` method.

The log method receives an `UncaughtGuardException` object, which contains all the necessary details about the uncaught
exception, including the stack trace, request data, and the unique trace identifier.

## 🏗️ Built-in Logging Strategies

The library comes with several built-in logging strategies that you can use out of the box. These strategies may require you to add additional dependencies to your project, depending on the strategy you choose.

### 📦 System.err Logging Strategy

The `System.err` logging strategy is the default logging strategy that logs uncaught exceptions to the standard error.
It does not require any additional dependencies and is already included in the core library in the `UncaughtGuardSystemErrLoggingStrategy` implementation class.
The `System.err` logging strategy is also automatically used as a fallback if no other logging strategies are provided or if all other strategies fail to log the exception.

In order to use the `System.err` logging strategy, ensure that you have the `spring-uncaught-guard-core` dependency in your project, in your `pom.xml`:

```xml
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then, simply add the `@EnableUncaughtGuard` annotation to your main Spring Boot application class, and it will be used by default:

```java
@SpringBootApplication
@EnableUncaughtGuard
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

You can also explicitly specify the `System.err` logging strategy in the `@EnableUncaughtGuard` annotation:

```java
@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {UncaughtGuardSystemErrLoggingStrategy.class}
)
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

### 📦 Java Logger Logging Strategy

The `Java Logger` logging strategy uses Java's built-in `java.util.logging.Logger` to log uncaught exceptions.
It does not require any additional dependencies and is already included in the core library in the `UncaughtGuardJavaLoggerLoggingStrategy` implementation class.

In order to use the `Java Logger` logging strategy, ensure that you have the `spring-uncaught-guard-core` dependency in your project, in your `pom.xml`:

```xml
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then, simply add the `@EnableUncaughtGuard` annotation to your main Spring Boot application class and specify the `UncaughtGuardJavaLoggerLoggingStrategy` logging strategy in the `loggingStrategies` attribute:

```java
@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {UncaughtGuardJavaLoggerLoggingStrategy.class}
)
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

### 📦 SLF4J Logging Strategy

The `SLF4J` logging strategy uses the SLF4J API to log uncaught exceptions. It requires the SLF4J dependency to be included in your project.
In order to use the `SLF4J` logging strategy, ensure first of all that you have the core library dependency in your project, in your `pom.xml`.
You will then also need to add the `spring-uncaught-guard-slf4j-strategy` dependency that includes the SLF4J dependency and the `UncaughtGuardSlf4jLoggingStrategy` implementation class:

```xml
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-slf4j-strategy</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then, simply add the `@EnableUncaughtGuard` annotation to your main Spring Boot application class and specify the `UncaughtGuardSlf4jLoggingStrategy` logging strategy in the `loggingStrategies` attribute:

```java
@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {UncaughtGuardSlf4jLoggingStrategy.class}
)
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```

### 📦 Kafka Logging Strategy

The `Kafka` logging strategy uses Apache Kafka to log uncaught exceptions, by sending a message in a Kafka topic queue. It requires the Kafka dependency to be included in your project.
In order to use the `Kafka` logging strategy, ensure first of all that you have the core library dependency in your project, in your `pom.xml`.
You will then also need to add the `spring-uncaught-guard-kafka-strategy` dependency that includes the Kafka dependency and the `UncaughtGuardKafkaAbstractLoggingStrategy` class.

```xml
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.velluto</groupId>
    <artifactId>spring-uncaught-guard-kafka-strategy</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

In case of the Kafka logging strategy, the dependency will expose an abstract class `UncaughtGuardKafkaAbstractLoggingStrategy` that you will need to extend and implement.
This is needed in order to specify the Kafka bootstrap servers and the topic name where the uncaught exceptions will be sent.

Create then a class that extends `UncaughtGuardKafkaAbstractLoggingStrategy` and implement the `getKafkaBootstrapServers()` and `getKafkaTopicName()` methods:
the first one will return the Kafka bootstrap servers addresses list, and the second one will return the topic name where the uncaught exceptions will be sent.

```java
import com.velluto.springuncaughtguard.kafka.UncaughtGuardKafkaAbstractLoggingStrategy;

public class MyUncaughtGuardKafkaLoggingStrategy extends UncaughtGuardKafkaAbstractLoggingStrategy {

    @Override
    protected List<String> getKafkaBootstrapServers() {
        return List.of("localhost:9092", "otherhost:9092"); // Replace with your Kafka bootstrap servers addresses
    }

    @Override
    protected String getKafkaTopicName() {
        return "uncaught-exceptions-topic"; // Replace with your Kafka topic name
    }
}
```

Then, simply add the `@EnableUncaughtGuard` annotation to your main Spring Boot application class and specify the name of Kafka logging strategy implementation class in the `loggingStrategies` attribute:

```java
@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {MyUncaughtGuardKafkaLoggingStrategy.class}
)
public class MySpringBootApplication { 
    public static void main(String[] args) {
                SpringApplication.run(MySpringBootApplication.class, args);
    }
}
```