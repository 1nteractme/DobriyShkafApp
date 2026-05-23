package com.iapp.iapp_messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/// Точка входа backend-приложения Spring Boot.
@SpringBootApplication
public class BackendApplication {

    /// Запускает Spring Boot приложение.
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}