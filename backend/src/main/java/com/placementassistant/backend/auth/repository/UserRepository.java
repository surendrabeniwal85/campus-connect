package com.placementassistant.backend.auth.repository;

import com.placementassistant.backend.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			update User user
			set user.passwordHash = :upgradedHash
			where lower(user.email) = lower(:email)
				and user.passwordHash = :existingHash
			""")
	int updatePasswordHashIfUnchanged(
			@Param("email") String email,
			@Param("existingHash") String existingHash,
			@Param("upgradedHash") String upgradedHash);
}
