package com.campusconnect.backend;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class JwtTestPropertySupport {

	private static final String TEST_SECRET = createTestSecret();

	@DynamicPropertySource
	public static void registerJwtSecret(DynamicPropertyRegistry registry) {
		registry.add("JWT_SECRET", () -> TEST_SECRET);
	}

	private static String createTestSecret() {
		byte[] secret = new byte[32];
		new SecureRandom().nextBytes(secret);
		return Base64.getEncoder().encodeToString(secret);
	}
}
