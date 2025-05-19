package com.example.greenhouseapplication.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // enables @Scheduled methods
public class GreenhouseBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenhouseBackendApplication.class, args);
    }
}
