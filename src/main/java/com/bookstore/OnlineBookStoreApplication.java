package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Online Bookstore application.
 *
 * <h2>Role in System Testing</h2>
 * <ul>
 *   <li>Bootstraps the full Spring Boot application context for system and end-to-end tests.</li>
 *   <li>Enables scheduled tasks via {@link EnableScheduling} for background jobs (if configured).</li>
 * </ul>
 *
 * @author Skach, Martin; Lavji, Fareen
 * @version 3.0
 * @since 2025-10-27
 */
@EnableScheduling
@SpringBootApplication
public class OnlineBookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }
}