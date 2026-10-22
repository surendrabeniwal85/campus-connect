package com.placementassistant.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.placementassistant.backend.auth.entity.User;
import com.placementassistant.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(PasswordHashUpgradeService.class)
class PasswordHashUpgradeServiceTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHashUpgradeService passwordHashUpgradeService;

	@Test
	void upgradesOnlyWhenTheStoredHashStillMatchesTheVerifiedLegacyHash() {
		User user = userRepository.saveAndFlush(new User(
				"Fictional Test User",
				"hash-upgrade@example.test",
				"legacy-hash"));

		boolean upgraded = passwordHashUpgradeService.upgradePasswordHash(
				user.getEmail(),
				"legacy-hash",
				"new-bcrypt-hash");
		boolean staleUpdate = passwordHashUpgradeService.upgradePasswordHash(
				user.getEmail(),
				"legacy-hash",
				"stale-bcrypt-hash");

		assertThat(upgraded).isTrue();
		assertThat(staleUpdate).isFalse();
		assertThat(userRepository.findById(user.getId()).orElseThrow().getPasswordHash())
				.isEqualTo("new-bcrypt-hash");
	}
}
