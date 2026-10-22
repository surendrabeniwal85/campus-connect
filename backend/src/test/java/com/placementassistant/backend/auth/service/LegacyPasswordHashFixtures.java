package com.placementassistant.backend.auth.service;

import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class LegacyPasswordHashFixtures {

	private LegacyPasswordHashFixtures() {
	}

	static String hash(String rawPassword) throws GeneralSecurityException {
		byte[] salt = new byte[16];
		for (int index = 0; index < salt.length; index++) {
			salt[index] = (byte) index;
		}
		PBEKeySpec keySpec = new PBEKeySpec(rawPassword.toCharArray(), salt, 100_000, 256);
		try {
			byte[] derivedKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
					.generateSecret(keySpec)
					.getEncoded();
			return "pbkdf2-sha256$100000$"
					+ Base64.getEncoder().encodeToString(salt) + "$"
					+ Base64.getEncoder().encodeToString(derivedKey);
		} finally {
			keySpec.clearPassword();
		}
	}
}
