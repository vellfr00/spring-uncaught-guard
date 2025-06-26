# 🌱 Spring Uncaught Guard

## 📝 Overview

Spring Uncaught Guard is a powerful and easy-to-use library that empowers you to seamlessly intercept and log uncaught exceptions in your Spring REST applications.  
With a focus on simplicity and developer productivity, it enables you to build robust applications without the hassle of manual exception management.

### 🚀 Key Features
- 🚨 **Automatic Exception Handling**: Effortlessly intercepts and logs uncaught runtime exceptions in your Spring REST applications, ensuring no error goes unnoticed.
- 📝 **Verbose Logging**: Delivers comprehensive logs for every exception, including stack traces and detailed request data such as headers, cookies, and body content.
- ⚡ **Zero Configuration Required**: Instantly operational with minimal setup—just add the `@EnableUncaughtGuard` annotation to your main application class and you’re ready to go.
- 🐞 **Debug-Friendly**: Assigns a unique identifier to each exception, which is returned in the error response, making it straightforward to trace and debug issues across distributed systems.
- 🛠️ **Highly Customizable**: Offers flexible customization options directly through the annotation, allowing you to adapt the library’s behavior to your specific needs.
- 🔌 **Easily Extensible**: Provides extension points for custom exception logging, other than the ones already provided out-of-the-box, enabling seamless integration with your existing logging and monitoring infrastructure.

## 🗂️ Project Structure
- 📦 `uncaught-guard`: Contains the core library code.
- 🧪 `uncaught-guard-test-app`: A sample Spring Boot application that demonstrates the library in action and serves as a testing ground.

## 🛠️ Usage

Add the `@EnableUncaughtGuard` annotation to your main Spring Boot application class to enable automatic uncaught exception handling:

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

### ⚙️ Annotation Features

If you need more control over the behavior of the uncaught exception handling, you can customize it using the `@EnableUncaughtGuard` annotation.
The `@EnableUncaughtGuard` annotation provides several customization options:

- 📝 **loggingStrategies**: Specify one or more logging strategies (implementations of `UncaughtGuardLoggingStrategy`). Default: logs to System.err. You can use built-in strategies or create your own custom logging strategy.
- 🚫 **excludedExceptions**: List exception types (subclasses of `RuntimeException`) to exclude from automatic handling.
- 💬 **httpResponseErrorMessage**: Customize the error message returned in the HTTP response.
- 🪵 **logErrorMessage**: Customize the error message that is logged.
- 🔁 **keepThrowingExceptions**: If true, rethrows the exception after handling (disables the custom HTTP response and traceId).
- 📦 **enableLogRequestBody**: If true (default), enables logging of the HTTP request body (may impact performance, but if not enabled you will miss request body logging).

Advanced example:

```java
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

## 🪵 Logging Strategies

You can use built-in logging strategies or create your own custom logging strategy by implementing the `UncaughtGuardLoggingStrategy` interface.

### 🏗️ Built-in Logging Strategies

Here are listed the built-in logging strategies:
- 🖥️ **UncaughtGuardSystemErrLoggingStrategy**: Logs uncaught exceptions to `System.err`.
- 📋 **UncaughtGuardSlf4jLoggingStrategy**: Logs uncaught exceptions using SLF4J (requires SLF4J dependency).
