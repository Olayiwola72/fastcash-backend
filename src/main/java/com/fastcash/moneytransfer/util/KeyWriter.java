package com.fastcash.moneytransfer.util;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyWriter {
	void savePublicKey(PublicKey publicKey, String publicKeyPath) throws Exception;
	
    void savePrivateKey(PrivateKey privateKey, String privateKeyPath) throws Exception;
}
