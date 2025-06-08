package com.educagestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for EducaGestor360
 * 
 * This class serves as the entry point for the Spring Boot application.
 * It enables JPA auditing for automatic timestamp management and
 * transaction management for database operations.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class EducaGestorApplication {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EducaGestorApplication.class, args);
    }
}
