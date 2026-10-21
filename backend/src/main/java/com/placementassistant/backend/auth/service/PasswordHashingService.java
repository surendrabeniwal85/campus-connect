package com.placementassistant.backend.auth.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashingService {

	private static final String HASH_PREFIX = "pbkdf2-sha256";
	private static final String BCRYPT_PREFIX = "$2";
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int MINIMUM_VERIFICATION_ITERATIONS = 100_000;
	private static final int MAXIMUM_VERIFICATION_ITERATIONS = 1_000_000;
	private static final int SALT_LENGTH_BYTES = 16;
	private static final int KEY_LENGTH_BITS = 256;

	private final BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();

	public String hashPassword(String rawPassword) {
		Objects.requireNonNull(rawPassword, "rawPassword must not be null");

		return this.bcryptPasswordEncoder.encode(rawPassword);
	}

	public boolean verifyPassword(String rawPassword, String storedHash) {
		if (rawPassword == null || storedHash == null) {
			return false;
		}
		if (storedHash.startsWith(BCRYPT_PREFIX)) {
			try {
				return this.bcryptPasswordEncoder.matches(rawPassword, storedHash);
			} catch (IllegalArgumentException exception) {
				return false;
			}
		}
		return verifyLegacyPassword(rawPassword, storedHash);
	}

	public boolean needsRehash(String storedHash) {
		return storedHash != null && storedHash.startsWith(HASH_PREFIX + "$");
	}

	private boolean verifyLegacyPassword(String rawPassword, String storedHash) {
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
