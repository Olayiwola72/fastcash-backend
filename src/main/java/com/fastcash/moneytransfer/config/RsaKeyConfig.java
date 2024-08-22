package com.fastcash.moneytransfer.config;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;

@Configuration
public class RsaKeyConfig {
    private final KeyPairFileUtil keyPairUtil;
    private final RSAKeyPairGenerator keyPairGenerator;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RsaKeyConfig( 
        @Value("${rsa.public-key}") String publicKeyPath,
        @Value("${rsa.private-key}") String privateKeyPath,
        RSAKeyPairGenerator keyPairGenerator,
        KeyPairFileUtil keyPairUtil
    ) throws Exception {
        this.keyPairUtil = keyPairUtil;
        this.keyPairGenerator = keyPairGenerator;

        // Check if key pair files exist
        if (checkKeyPairExistence(publicKeyPath, privateKeyPath)) {
            // If key pair files exist, load the keys
            loadKeys(publicKeyPath, privateKeyPath);
        } else {
            // If key pair files don't exist, generate new keys and save them
            generateAndSaveKeyPair(publicKeyPath, privateKeyPath);
        }
    }

    // Check if key pair files exist
    private boolean checkKeyPairExistence(String publicKeyPath, String privateKeyPath) {
        return keyPairUtil.areKeysExisting(publicKeyPath, privateKeyPath);
    }

    // Load public and private keys from existing files
    private void loadKeys(String publicKeyPath, String privateKeyPath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.publicKey = (RSAPublicKey) keyPairUtil.getPublicKey(publicKeyPath);
        this.privateKey = (RSAPrivateKey) keyPairUtil.getPrivateKey(privateKeyPath);
    }

    // Generate new key pair, save keys to files, and set the public and private keys
    private void generateAndSaveKeyPair(String publicKeyPath, String privateKeyPath) throws Exception {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Save generated keys to files
        saveKeys(publicKeyPath, privateKeyPath);
    }

    // Save public and private keys to files
    private void saveKeys(String publicKeyPath, String privateKeyPath) throws Exception {
    	keyPairUtil.savePublicKey(publicKey, publicKeyPath);
    	keyPairUtil.savePrivateKey(privateKey, privateKeyPath);
    }

    // Getter for the public key
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    // Getter for the private key
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }
}
