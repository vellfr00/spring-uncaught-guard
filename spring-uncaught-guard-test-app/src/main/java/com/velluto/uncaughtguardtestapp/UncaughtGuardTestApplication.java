package com.velluto.uncaughtguardtestapp;

import com.velluto.uncaughtguard.annotations.EnableUncaughtGuard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableUncaughtGuard
public class UncaughtGuardTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UncaughtGuardTestApplication.class, args);
    }
}
