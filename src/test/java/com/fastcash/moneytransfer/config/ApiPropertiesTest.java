package com.fastcash.moneytransfer.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiPropertiesTest {

    private ApiProperties apiProperties;

    @BeforeEach
    void setUp() {
        // Initialize ApiProperties with test data
        apiProperties = new ApiProperties(
            "/v1", "/api", "/config", "/auth", "/token", "/refresh", "/login",
            "/user", "/account", "/transfer", "/password", "/forgot", "/reset", "/exchange-rate", "/reset"
        );
    }

    @Test
    void testFullConfigPath() {
        String expected = "/api/v1/config";
        assertEquals(expected, apiProperties.fullConfigPath());
    }

    @Test
    void testFullAuthPath() {
        String expected = "/api/v1/auth";
        assertEquals(expected, apiProperties.fullAuthPath());
    }

    @Test
    void testFullTokenPath() {
        String expected = "/api/v1/auth/token";
        assertEquals(expected, apiProperties.fullTokenPath());
    }

    @Test
    void testFullTokenRefreshPath() {
        String expected = "/api/v1/auth/refresh";
        assertEquals(expected, apiProperties.fullTokenRefreshPath());
    }

    @Test
    void testFullLoginPath() {
        String expected = "/api/v1/auth/login";
        assertEquals(expected, apiProperties.fullLoginPath());
    }

    @Test
    void testFullUserPath() {
        String expected = "/api/v1/user";
        assertEquals(expected, apiProperties.fullUserPath());
    }

    @Test
    void testFullAccountPath() {
        String expected = "/api/v1/account";
        assertEquals(expected, apiProperties.fullAccountPath());
    }

    @Test
    void testFullTransferPath() {
        String expected = "/api/v1/transfer";
        assertEquals(expected, apiProperties.fullTransferPath());
    }

    @Test
    void testFullPasswordPath() {
        String expected = "/api/v1/password";
        assertEquals(expected, apiProperties.fullPasswordPath());
    }

    @Test
    void testFullPasswordForgotPath() {
        String expected = "/api/v1/password/forgot";
        assertEquals(expected, apiProperties.fullPasswordForgotPath());
    }

    @Test
    void testFullPasswordResetPath() {
        String expected = "/api/v1/password/reset";
        assertEquals(expected, apiProperties.fullPasswordResetPath());
    }

    @Test
    void testFullExchangeRatePath() {
        String expected = "/api/v1/exchange-rate";
        assertEquals(expected, apiProperties.fullExchangeRatePath());
    }
    
}


