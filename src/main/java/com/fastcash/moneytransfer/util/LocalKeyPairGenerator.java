package com.fastcash.moneytransfer.util;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface LocalKeyPairGenerator {
	KeyPair generateKeyPair() throws NoSuchAlgorithmException;
}