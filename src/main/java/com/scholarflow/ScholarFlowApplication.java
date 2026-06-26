package com.scholarflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScholarFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholarFlowApplication.class, args);
    }
}
