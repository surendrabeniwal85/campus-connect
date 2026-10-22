package com.placementassistant.backend.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class RoleNameTests {

	@Test
	void definesTheSevenCanonicalRolesWithTheirPortalsAndAuthorities() {
		assertThat(EnumSet.allOf(RoleName.class))
				.containsExactly(
						RoleName.STUDENT,
						RoleName.TEACHER,
						RoleName.HOD,
						RoleName.DIRECTOR_HR,
						RoleName.TPO,
						RoleName.D_TPO,
						RoleName.PLACEMENT_COORDINATOR);
		assertThat(EnumSet.allOf(RoleName.class))
				.filteredOn(role -> role.portal() == Portal.INSTITUTE)
				.containsExactly(RoleName.STUDENT, RoleName.TEACHER, RoleName.HOD);
		assertThat(EnumSet.allOf(RoleName.class))
				.filteredOn(role -> role.portal() == Portal.PLACEMENT)
				.containsExactly(
						RoleName.DIRECTOR_HR,
						RoleName.TPO,
						RoleName.D_TPO,
						RoleName.PLACEMENT_COORDINATOR);
		assertThat(RoleName.D_TPO.authorityName()).isEqualTo("ROLE_D_TPO");
		assertThat(EnumSet.allOf(RoleName.class))
				.extracting(RoleName::authorityName)
				.doesNotContain("ROLE_ADMIN", "ROLE_FACULTY");
	}

	@Test
	void rejectsUnknownAndCrossPortalAuthorities() {
		assertThat(RoleName.allBelongToPortal(
				java.util.List.of("ROLE_STUDENT"),
				Portal.INSTITUTE)).isTrue();
		assertThat(RoleName.allBelongToPortal(
				java.util.List.of("ROLE_STUDENT"),
				Portal.PLACEMENT)).isFalse();
		assertThat(RoleName.allBelongToPortal(
				java.util.List.of("ROLE_D_TPO"),
				Portal.PLACEMENT)).isTrue();
		assertThat(RoleName.allBelongToPortal(
				java.util.List.of("ROLE_DEPUTY_TPO"),
				Portal.PLACEMENT)).isFalse();
		assertThat(RoleName.allBelongToPortal(
				java.util.List.of("ROLE_ADMIN"),
				Portal.INSTITUTE)).isFalse();
		assertThat(RoleName.allBelongToPortal(java.util.List.of(), Portal.INSTITUTE)).isFalse();
	}
}
