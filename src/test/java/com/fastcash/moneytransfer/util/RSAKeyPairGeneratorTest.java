package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.KeyPair;

import org.junit.jupiter.api.Test;

class RSAKeyPairGeneratorTest {
	
	@Test
	public void testGenerateKeyPair() {
        // Arrange
		LocalKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();

        // Act and Assert
        assertDoesNotThrow(() -> {
        	KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Additional assertions
            assertNotNull(keyPair);
            assertNotNull(keyPair.getPublic());
            assertNotNull(keyPair.getPrivate());
        });
    }
	
}
