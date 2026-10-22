package com.placementassistant.backend.auth.service;

import com.placementassistant.backend.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordHashUpgradeService {

	private final UserRepository userRepository;

	public PasswordHashUpgradeService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public boolean upgradePasswordHash(String email, String existingHash, String upgradedHash) {
		return this.userRepository.updatePasswordHashIfUnchanged(email, existingHash, upgradedHash) == 1;
	}
}
