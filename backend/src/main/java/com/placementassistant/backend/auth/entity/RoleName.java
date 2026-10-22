package com.placementassistant.backend.auth.entity;

import java.util.Collection;

public enum RoleName {
	STUDENT(Portal.INSTITUTE),
	TEACHER(Portal.INSTITUTE),
	HOD(Portal.INSTITUTE),
	DIRECTOR_HR(Portal.PLACEMENT),
	TPO(Portal.PLACEMENT),
	D_TPO(Portal.PLACEMENT),
	PLACEMENT_COORDINATOR(Portal.PLACEMENT);

	private final Portal portal;

	RoleName(Portal portal) {
		this.portal = portal;
	}

	public Portal portal() {
		return this.portal;
	}

	public String authorityName() {
		return "ROLE_" + name();
	}

	public static boolean allBelongToPortal(Collection<String> authorities, Portal portal) {
		if (authorities == null || authorities.isEmpty() || portal == null) {
			return false;
		}
		return authorities.stream().allMatch(authority -> {
			if (authority == null || !authority.startsWith("ROLE_")) {
				return false;
			}
			try {
				return valueOf(authority.substring("ROLE_".length())).portal == portal;
			} catch (IllegalArgumentException | IndexOutOfBoundsException exception) {
				return false;
			}
		});
	}
}
