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
- 🔌 **Easily Extensible**: Provides extension points for custom exception logging, other than the ones already provided, enabling seamless integration with your existing logging and monitoring infrastructure.

### ⚙️ How It Works
Spring Uncaught Guard utilizes Spring's `@RestControllerAdvice` mechanism to seamlessly intercept uncaught exceptions thrown by your REST controllers. By default, it is configured to handle all exceptions that are subclasses of `RuntimeException`, which are commonly used for application-specific errors.
If you have defined a custom `@RestControllerAdvice` for a specific exception type, your custom handler will take precedence, and those exceptions will not be intercepted by this library. This ensures you retain full control and flexibility over exception handling for known cases, while Spring Uncaught Guard acts as a safety net for truly unexpected errors.
When an uncaught exception occurs, the library automatically captures comprehensive details—including the stack trace, request headers, cookies, and body content—and assigns a unique trace identifier (`traceId`) to the event. These details are then logged using your chosen logging strategies, and a standardized error response containing the `traceId` is returned to the client, making debugging and error tracking straightforward.

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

## 🪵 Logging Strategies

You can use provided logging strategies or create your own custom logging strategy by extending the abstract `UncaughtGuardLoggingStrategy` class
and implementing the abstract `log` method. This method receives an `UncaughtGuardException` object, which contains all the necessary details about the uncaught exception, including the stack trace, request data, and the unique trace identifier.

### 🏗️ Built-in Logging Strategies

Here are listed the provided logging strategies:

- 🖥️ **UncaughtGuardSystemErrLoggingStrategy**: Logs uncaught exceptions to `System.err`.
