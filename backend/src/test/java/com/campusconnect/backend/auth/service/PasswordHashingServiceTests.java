package com.campusconnect.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class PasswordHashingServiceTests {

	private final PasswordHashingService passwordHashingService = new PasswordHashingService();

	@Test
	void verifiesPasswordAgainstItsHash() {
		String hash = passwordHashingService.hashPassword("correct horse battery staple");

		assertThat(passwordHashingService.verifyPassword(
				"correct horse battery staple",
				hash)).isTrue();
	}

	@Test
	void rejectsIncorrectPassword() {
		String hash = passwordHashingService.hashPassword("correct horse battery staple");

		assertThat(passwordHashingService.verifyPassword("incorrect password", hash)).isFalse();
	}

	@Test
	void repeatedHashingUsesDifferentRandomSalts() {
		String firstHash = passwordHashingService.hashPassword("same password");
		String secondHash = passwordHashingService.hashPassword("same password");

		assertThat(firstHash).isNotEqualTo(secondHash);
		assertThat(passwordHashingService.verifyPassword("same password", firstHash)).isTrue();
		assertThat(passwordHashingService.verifyPassword("same password", secondHash)).isTrue();
	}

	@Test
	void rejectsMalformedOrUnsupportedStoredHashes() {
		assertThat(passwordHashingService.verifyPassword("password", null)).isFalse();
		assertThat(passwordHashingService.verifyPassword(null, "not-a-hash")).isFalse();
		assertThat(passwordHashingService.verifyPassword("password", "sha256$1$salt$hash")).isFalse();
		assertThat(passwordHashingService.verifyPassword("password", "pbkdf2-sha256$999999999$"
				+ Base64.getEncoder().encodeToString(new byte[16]) + "$"
				+ Base64.getEncoder().encodeToString(new byte[32]))).isFalse();
	}
}
