package com.placementassistant.backend.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.placementassistant.backend.auth.entity.Role;
import com.placementassistant.backend.auth.entity.User;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class AuthRepositoryTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void persistsUserRoleAssociationAndCreationTimestamp() {
		Role studentRole = roleRepository.saveAndFlush(new Role("ROLE_STUDENT"));
		User user = new User("Test Student", "student@example.test", "test-password-hash");
		user.setEnabled(false);
		user.addRole(studentRole);

		User savedUser = userRepository.saveAndFlush(user);
		Long userId = savedUser.getId();
		entityManager.clear();

		User reloadedUser = userRepository.findById(userId).orElseThrow();
		assertThat(reloadedUser.getDisplayName()).isEqualTo("Test Student");
		assertThat(reloadedUser.getEmail()).isEqualTo("student@example.test");
		assertThat(reloadedUser.getPasswordHash()).isEqualTo("test-password-hash");
		assertThat(reloadedUser.isEnabled()).isFalse();
		assertThat(reloadedUser.getCreatedAt()).isNotNull();
		assertThat(reloadedUser.getRoles()).extracting(Role::getName)
				.containsExactly("ROLE_STUDENT");
	}

	@Test
	void looksUpUserByEmailWithoutCaseSensitivity() {
		userRepository.saveAndFlush(new User(
				"Test Student",
				"student@example.test",
				"test-password-hash"));

		Optional<User> foundUser = userRepository.findByEmailIgnoreCase("STUDENT@EXAMPLE.TEST");

		assertThat(foundUser).isPresent();
		assertThat(foundUser.orElseThrow().getEmail()).isEqualTo("student@example.test");
	}

	@Test
	void looksUpRoleByExactName() {
		roleRepository.saveAndFlush(new Role("ROLE_TPO"));

		Optional<Role> foundRole = roleRepository.findByName("ROLE_TPO");

		assertThat(foundRole).isPresent();
		assertThat(foundRole.orElseThrow().getName()).isEqualTo("ROLE_TPO");
	}
}
