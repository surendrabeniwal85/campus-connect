package com.placementassistant.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class JwtSecurityConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withUserConfiguration(JwtSecurityConfiguration.class);

	@Test
	void contextFailsWhenJwtSecretIsMissing() {
		this.contextRunner.run(context -> {
			assertThat(context).hasFailed();
			assertThat(rootCause(context.getStartupFailure()).getMessage())
					.contains("JWT_SECRET");
		});
	}

	@Test
	void contextFailsWhenJwtSecretIsTooShort() {
		this.contextRunner
				.withPropertyValues("JWT_SECRET=short")
				.run(context -> {
					assertThat(context).hasFailed();
					assertThat(rootCause(context.getStartupFailure()))
							.hasMessage("JWT_SECRET must contain at least 32 UTF-8 bytes.");
				});
	}

	private static Throwable rootCause(Throwable failure) {
		Throwable cause = failure;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}
}
