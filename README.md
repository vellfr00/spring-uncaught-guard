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