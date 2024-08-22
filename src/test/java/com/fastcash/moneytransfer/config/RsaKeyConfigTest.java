package com.fastcash.moneytransfer.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;

@SpringBootTest
class RsaKeyConfigTest {
	@Value("${rsa.public-key}") 
	private String publicKeyPath;
	
	@Value("${rsa.private-key}") 
	private String privateKeyPath;
	
	@Autowired
	private RSAKeyPairGenerator keyPairGenerator;
	
	@Autowired
	private KeyPairFileUtil keyPairUtil;

	@Test
    public void testKeyPairGenerationAndLoading() throws Exception {
        // Ensure key files do not exist before running the test
        Files.deleteIfExists(Paths.get(publicKeyPath));
        Files.deleteIfExists(Paths.get(privateKeyPath));
        Files.deleteIfExists(Paths.get("src/main/resources/certs"));

        // Create a new instance of RsaKeyConfig
        RsaKeyConfig rsaKeyConfig = new RsaKeyConfig(publicKeyPath, privateKeyPath, keyPairGenerator, keyPairUtil);

        // Get the generated key pair
        RSAPublicKey publicKey = rsaKeyConfig.getPublicKey();
        RSAPrivateKey privateKey = rsaKeyConfig.getPrivateKey();

        // Check that the key pair is not null
        assertNotNull(publicKey);
        assertNotNull(privateKey);

        // Check that the key files now exist
        assertTrue(Files.exists(Paths.get(publicKeyPath)));
        assertTrue(Files.exists(Paths.get(privateKeyPath)));
    }
}
