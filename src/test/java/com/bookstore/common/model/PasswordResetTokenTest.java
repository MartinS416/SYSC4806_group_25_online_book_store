package com.bookstore.common.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordResetToken}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Domain model helper.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Validate the expiry calculation performed by {@link PasswordResetToken#isExpired()}.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2> Java time API ({@link LocalDateTime}) only.
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
class PasswordResetTokenTest {

    /**
     * Test: {@link PasswordResetToken#isExpired()} returns true when the expiryDate is in the past.
     */
    @Test
    @DisplayName("isExpired returns true when expiryDate is in the past")
    void isExpired_trueWhenPast() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        assertTrue(token.isExpired());
    }

    /**
     * Test: {@link PasswordResetToken#isExpired()} returns false when the expiryDate is in the future.
     */
    @Test
    @DisplayName("isExpired returns false when expiryDate is in the future")
    void isExpired_falseWhenFuture() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        assertFalse(token.isExpired());
    }
}