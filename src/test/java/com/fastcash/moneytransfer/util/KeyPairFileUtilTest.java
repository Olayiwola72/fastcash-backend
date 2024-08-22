package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyPairFileUtilTest {
	
	private KeyPairFileUtil keyPairFileUtil;

    @BeforeEach
    void setUp() {
        keyPairFileUtil = new KeyPairFileUtil();
    }
    
    @Test
    void testAreKeysExisting() throws IOException {
        // Arrange
    	String publicKeyPath = "public_key.pem";
		String privateKeyPath = "private_key.pem";

		// Act
		boolean keyFilesExist = keyPairFileUtil.areKeysExisting(publicKeyPath, privateKeyPath);

		// Assert
		assertFalse(keyFilesExist); // Assuming the files do not exist initially
		
        // Create mock file existence
        Files.createFile(Path.of(publicKeyPath));
        Files.createFile(Path.of(privateKeyPath));
        
        // Act
     	keyFilesExist = keyPairFileUtil.areKeysExisting(publicKeyPath, privateKeyPath);
     	
     	// Assert
     	assertTrue(keyFilesExist); // Assuming the files do not exist initially
     	
     	// Clean up generated files
     	Files.deleteIfExists(Path.of(publicKeyPath));
     	Files.deleteIfExists(Path.of(privateKeyPath));
    }
    
    @Test
	void testPublicKeyAndPrivateKeyOperations() throws Exception {
		// Arrange
		String publicKeyPath = "public_key.pem";
		String privateKeyPath = "private_key.pem";
		
		// Generate key pair for testing
		RSAKeyPairGenerator rSAKeyPairGenerator = new RSAKeyPairGenerator();
		KeyPair keyPair = rSAKeyPairGenerator.generateKeyPair();	
		
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// Act
		keyPairFileUtil.savePublicKey(publicKey, publicKeyPath);
		keyPairFileUtil.savePrivateKey(privateKey, privateKeyPath);
        PublicKey retrievedPublicKey = keyPairFileUtil.getPublicKey(publicKeyPath);
        PrivateKey retrievedPrivateKey = keyPairFileUtil.getPrivateKey(privateKeyPath);

		// Assert
        assertNotNull(retrievedPublicKey);
        assertNotNull(retrievedPrivateKey);
		assertTrue(Files.exists(Path.of(publicKeyPath)));
		assertTrue(Files.exists(Path.of(privateKeyPath)));

		// Verify content of generated files
		assertEquals(publicKey, retrievedPublicKey);
		assertEquals(privateKey, retrievedPrivateKey);

		// Clean up generated files
		Files.deleteIfExists(Path.of(publicKeyPath));
		Files.deleteIfExists(Path.of(privateKeyPath));
	}
    
    @Test
	void testWritePemFile() throws Exception {
		// Arrange
		String pemFilePath = "test_file.pem";
		String pemContent = "Test content";

		// Act
		keyPairFileUtil.writePemFile(pemContent, pemFilePath);

		// Assert
		assertTrue(Files.exists(Path.of(pemFilePath)));

		// Verify content of generated file
		String savedContent = new String(Files.readAllBytes(Path.of(pemFilePath)));
		assertEquals(pemContent, savedContent.trim());

		// Clean up generated file
		Files.deleteIfExists(Path.of(pemFilePath));
	}
}
