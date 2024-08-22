package com.fastcash.moneytransfer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class KeyPairFileUtil implements KeyExist, KeyReader, KeyWriter {
	@Override
	public boolean areKeysExisting(String publicKeyPath, String privateKeyPath) {
		return Files.exists(Paths.get(publicKeyPath)) && Files.exists(Paths.get(privateKeyPath));
	}
	
	@Override
	public PublicKey getPublicKey(String publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
        String keyString = new String(keyBytes);

        // Decode the Base64 encoded key
        byte[] decodedKey = Base64.getDecoder().decode(keyString);

        // Generate the public key object
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
	
	@Override
	public void savePublicKey(PublicKey publicKey, String publicKeyPath) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        String pem = Base64.getEncoder().encodeToString(keySpec.getEncoded());
        writePemFile(pem, publicKeyPath);
    }
	
	@Override
	public PrivateKey getPrivateKey(String privateKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        String keyString = new String(keyBytes);

        // Decode the Base64 encoded key
        byte[] decodedKey = Base64.getDecoder().decode(keyString);

        // Generate the private key object
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
	
	@Override
	public void savePrivateKey(PrivateKey privateKey, String privateKeyPath) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        String pem = Base64.getEncoder().encodeToString(keySpec.getEncoded());
        writePemFile(pem, privateKeyPath);
    }

	void writePemFile(String pem, String filepath) throws Exception {
		File file = new File(filepath);
		FileUtils.writeStringToFile(file, pem, "UTF-8");
    }
	
}
