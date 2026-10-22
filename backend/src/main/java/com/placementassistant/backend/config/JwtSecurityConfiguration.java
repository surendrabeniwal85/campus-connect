package com.placementassistant.backend.config;

import com.placementassistant.backend.auth.service.JwtTokenService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;

@Configuration
public class JwtSecurityConfiguration {

	@Bean
	SecretKey jwtSigningKey(@Value("${JWT_SECRET}") String jwtSecret) {
		byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < 32) {
			throw new IllegalStateException("JWT_SECRET must contain at least 32 UTF-8 bytes.");
		}
		return new SecretKeySpec(secretBytes, "HmacSHA256");
	}

	@Bean
	Clock jwtClock() {
		return Clock.systemUTC();
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey));
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSigningKey, Clock jwtClock) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSigningKey)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
		JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ZERO);
		timestampValidator.setClock(jwtClock);
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(JwtTokenService.ISSUER),
				timestampValidator,
				jwt -> this.validateExpectedClaims(jwt, jwtClock)));
		return decoder;
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		authoritiesConverter.setAuthoritiesClaimName("roles");
		authoritiesConverter.setAuthorityPrefix("");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
		converter.setPrincipalClaimName(JwtClaimNames.SUB);
		return converter;
	}

	private OAuth2TokenValidatorResult validateExpectedClaims(Jwt jwt, Clock clock) {
		Object rolesClaim = jwt.getClaims().get("roles");
		boolean validRoles = rolesClaim instanceof List<?> roles
				&& !roles.isEmpty()
				&& roles.stream().allMatch(role ->
						role instanceof String value && value.startsWith("ROLE_") && value.length() > 5);
		boolean validLifetime = jwt.getIssuedAt() != null
				&& jwt.getExpiresAt() != null
				&& jwt.getExpiresAt().isAfter(jwt.getIssuedAt())
				&& !jwt.getIssuedAt().isAfter(Instant.now(clock))
				&& !jwt.getExpiresAt().isAfter(
						jwt.getIssuedAt().plusSeconds(JwtTokenService.TOKEN_TTL_SECONDS));
		if (StringUtils.hasText(jwt.getSubject())
				&& jwt.getAudience().contains(JwtTokenService.AUDIENCE)
				&& validRoles
				&& validLifetime) {
			return OAuth2TokenValidatorResult.success();
		}
		return OAuth2TokenValidatorResult.failure(
				new OAuth2Error("invalid_token", "Token claims are invalid.", null));
	}
}
