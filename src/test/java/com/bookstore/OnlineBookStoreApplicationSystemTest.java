package com.bookstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * System-level smoke test for the Online Bookstore application.
 *
 * <h2>Test Category:</h2> System Tests (ST) / Smoke.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Verify that the full Spring Boot application context loads without failures.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2>
 * Full application configuration and auto-configuration; no explicit collaborators.
 *
 * @author Skach, Martin; Lavji, Fareen
 * @version 3.0
 * @since 2025-10-27
 */
@SpringBootTest
@DisplayName("OnlineBookStoreApplication System Smoke Test")
class OnlineBookStoreApplicationSystemTest {

    /**
     * Test: application context loads successfully with all beans.
     */
    @Test
    @DisplayName("contextLoads")
    void contextLoads() {
        // Intentionally empty – failure to start context will fail this test.
    }
}