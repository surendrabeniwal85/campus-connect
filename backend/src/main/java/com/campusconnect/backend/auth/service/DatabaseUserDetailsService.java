package com.campusconnect.backend.auth.service;

import com.campusconnect.backend.auth.entity.User;
import com.campusconnect.backend.auth.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";

	private final UserRepository userRepository;

	public DatabaseUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(username)
				.filter(User::isEnabled)
				.orElseThrow(() -> new UsernameNotFoundException(INVALID_CREDENTIALS_MESSAGE));

		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.authorities(user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.getName()))
						.toList())
				.build();
	}
}
