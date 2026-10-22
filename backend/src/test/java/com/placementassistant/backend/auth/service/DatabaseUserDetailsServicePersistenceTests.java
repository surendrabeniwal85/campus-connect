package com.placementassistant.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.placementassistant.backend.auth.entity.Role;
import com.placementassistant.backend.auth.entity.User;
import com.placementassistant.backend.auth.repository.RoleRepository;
import com.placementassistant.backend.auth.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(DatabaseUserDetailsService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DatabaseUserDetailsServicePersistenceTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DatabaseUserDetailsService userDetailsService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void loadsUserRolesWithinServiceTransaction() {
		String email = "auth-test-" + UUID.randomUUID() + "@example.test";
		Role role = roleRepository.saveAndFlush(new Role("ROLE_AUTH_TEST"));
		User user = userRepository.saveAndFlush(
				new User("Authentication Test", email, "stored-password-hash"));
		user.addRole(role);
		userRepository.saveAndFlush(user);

		try {
			var details = userDetailsService.loadUserByUsername(email);

			assertThat(details.getUsername()).isEqualTo(email);
			assertThat(details.getAuthorities())
					.extracting(authority -> authority.getAuthority())
					.containsExactly("ROLE_AUTH_TEST");
		} finally {
			jdbcTemplate.update("delete from user_role where user_id = ?", user.getId());
			userRepository.delete(user);
			roleRepository.delete(role);
		}
	}
}
