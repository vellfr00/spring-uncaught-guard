package com.velluto.uncaughtguardtestapp;

import com.velluto.uncaughtguard.annotations.EnableUncaughtGuard;
import com.velluto.uncaughtguard.strategies.UncaughtGuardJavaLoggerLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSlf4jLoggingStrategy;
import com.velluto.uncaughtguardtestapp.logging.strategies.UncaughtGuardFileSystemStrategy;
import com.velluto.uncaughtguardtestapp.logging.strategies.UncaughtGuardKafkaLoggingStrategy;
import com.velluto.uncaughtguardtestapp.logging.strategies.UncaughtGuardRestLoggingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableUncaughtGuard(
        loggingStrategies = {
                UncaughtGuardSlf4jLoggingStrategy.class,
                UncaughtGuardJavaLoggerLoggingStrategy.class,
                UncaughtGuardRestLoggingStrategy.class,
                UncaughtGuardKafkaLoggingStrategy.class,
                UncaughtGuardFileSystemStrategy.class
        }
)
public class UncaughtGuardTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UncaughtGuardTestApplication.class, args);
    }
}
