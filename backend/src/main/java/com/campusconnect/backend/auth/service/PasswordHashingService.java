package com.campusconnect.backend.auth.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashingService {

	private static final String HASH_PREFIX = "pbkdf2-sha256";
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 600_000;
	private static final int MINIMUM_VERIFICATION_ITERATIONS = 100_000;
	private static final int MAXIMUM_VERIFICATION_ITERATIONS = 1_000_000;
	private static final int SALT_LENGTH_BYTES = 16;
	private static final int KEY_LENGTH_BITS = 256;

	private final SecureRandom secureRandom = new SecureRandom();

	public String hashPassword(String rawPassword) {
		Objects.requireNonNull(rawPassword, "rawPassword must not be null");

		byte[] salt = new byte[SALT_LENGTH_BYTES];
		secureRandom.nextBytes(salt);
		byte[] derivedKey = deriveKey(rawPassword, salt, ITERATIONS);

		try {
			return HASH_PREFIX + "$" + ITERATIONS + "$"
					+ Base64.getEncoder().encodeToString(salt) + "$"
					+ Base64.getEncoder().encodeToString(derivedKey);
		} finally {
			java.util.Arrays.fill(derivedKey, (byte) 0);
		}
	}

	public boolean verifyPassword(String rawPassword, String storedHash) {
		if (rawPassword == null || storedHash == null) {
			return false;
		}

		String[] parts = storedHash.split("\\$", -1);
		if (parts.length != 4 || !HASH_PREFIX.equals(parts[0])) {
			return false;
		}

		int iterations;
		byte[] salt;
		byte[] expectedKey;
		try {
			iterations = Integer.parseInt(parts[1]);
			salt = Base64.getDecoder().decode(parts[2]);
			expectedKey = Base64.getDecoder().decode(parts[3]);
		} catch (IllegalArgumentException exception) {
			return false;
		}

		if (iterations < MINIMUM_VERIFICATION_ITERATIONS
				|| iterations > MAXIMUM_VERIFICATION_ITERATIONS
				|| salt.length != SALT_LENGTH_BYTES
				|| expectedKey.length != KEY_LENGTH_BITS / Byte.SIZE) {
			return false;
		}

		try {
			byte[] actualKey = deriveKey(rawPassword, salt, iterations);
			try {
				return MessageDigest.isEqual(expectedKey, actualKey);
			} finally {
				java.util.Arrays.fill(actualKey, (byte) 0);
			}
		} finally {
			java.util.Arrays.fill(expectedKey, (byte) 0);
		}
	}

	private byte[] deriveKey(String rawPassword, byte[] salt, int iterations) {
		char[] password = rawPassword.toCharArray();
		PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS);
		try {
			return SecretKeyFactory.getInstance(ALGORITHM)
					.generateSecret(keySpec)
					.getEncoded();
		} catch (GeneralSecurityException exception) {
			throw new IllegalStateException("Password hashing is unavailable.", exception);
		} finally {
			keySpec.clearPassword();
			java.util.Arrays.fill(password, '\0');
		}
	}
}
