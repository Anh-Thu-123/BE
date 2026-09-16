package com.nagare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NagareApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NagareApiApplication.class, args);
    }
}
