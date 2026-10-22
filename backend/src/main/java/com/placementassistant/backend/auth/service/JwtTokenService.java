package com.placementassistant.backend.auth.service;

import com.placementassistant.backend.auth.principal.AuthenticatedUserPrincipal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

	public static final String ISSUER = "campus-connect-api";
	public static final String AUDIENCE = "campus-connect-api";
	public static final long TOKEN_TTL_SECONDS = 900;

	private final JwtEncoder jwtEncoder;
	private final Clock clock;

	public JwtTokenService(JwtEncoder jwtEncoder, Clock clock) {
		this.jwtEncoder = jwtEncoder;
		this.clock = clock;
	}

	public String issue(AuthenticatedUserPrincipal principal) {
		Instant issuedAt = this.clock.instant();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(principal.getName())
				.audience(List.of(AUDIENCE))
				.issuedAt(issuedAt)
				.expiresAt(issuedAt.plusSeconds(TOKEN_TTL_SECONDS))
				.claim("roles", principal.roleNames())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long expiresInSeconds() {
		return TOKEN_TTL_SECONDS;
	}
}
