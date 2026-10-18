package com.campusconnect.backend.config;

import com.campusconnect.backend.auth.service.PasswordHashAuthenticationProvider;
import com.campusconnect.backend.auth.service.PasswordHashingService;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

@Configuration
public class SecurityConfiguration {

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new ChangeSessionIdAuthenticationStrategy();
	}

	@Bean
	SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	CookieCsrfTokenRepository csrfTokenRepository() {
		CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		repository.setCookieName("XSRF-TOKEN");
		repository.setHeaderName("X-XSRF-TOKEN");
		repository.setCookiePath("/");
		return repository;
	}

	@Bean
	CsrfTokenRequestHandler csrfTokenRequestHandler() {
		return new SpaCsrfTokenRequestHandler();
	}

	@Bean
	LogoutHandler logoutHandler(CookieCsrfTokenRepository csrfTokenRepository) {
		SecurityContextLogoutHandler securityContextLogoutHandler =
				new SecurityContextLogoutHandler();
		securityContextLogoutHandler.setInvalidateHttpSession(true);
		securityContextLogoutHandler.setClearAuthentication(true);
		return new CompositeLogoutHandler(
				securityContextLogoutHandler,
				new CookieClearingLogoutHandler("JSESSIONID"),
				(request, response, authentication) ->
						csrfTokenRepository.saveToken(null, request, response));
	}

	@Bean
	AuthenticationProvider authenticationProvider(
			UserDetailsService userDetailsService,
			PasswordHashingService passwordHashingService) {
		return new PasswordHashAuthenticationProvider(userDetailsService, passwordHashingService);
	}

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			AuthenticationProvider authenticationProvider,
			SessionAuthenticationStrategy sessionAuthenticationStrategy,
			SecurityContextRepository securityContextRepository,
			CookieCsrfTokenRepository csrfTokenRepository,
			CsrfTokenRequestHandler csrfTokenRequestHandler,
			CsrfAccessDeniedHandler csrfAccessDeniedHandler) throws Exception {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(csrfTokenRepository)
						.csrfTokenRequestHandler(csrfTokenRequestHandler))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.sessionAuthenticationStrategy(sessionAuthenticationStrategy))
				.securityContext(securityContext -> securityContext
						.securityContextRepository(securityContextRepository))
				.httpBasic(basic -> basic.disable())
				.formLogin(form -> form.disable())
				.authenticationProvider(authenticationProvider)
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
						.accessDeniedHandler(csrfAccessDeniedHandler))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/auth/csrf").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
						.anyRequest().authenticated());
		return http.build();
	}

	private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

		private final CsrfTokenRequestAttributeHandler plainHandler =
				new CsrfTokenRequestAttributeHandler();
		private final XorCsrfTokenRequestAttributeHandler xorHandler =
				new XorCsrfTokenRequestAttributeHandler();

		@Override
		public void handle(
				jakarta.servlet.http.HttpServletRequest request,
				jakarta.servlet.http.HttpServletResponse response,
				java.util.function.Supplier<CsrfToken> csrfToken) {
			this.xorHandler.handle(request, response, csrfToken);
			csrfToken.get();
		}

		@Override
		public String resolveCsrfTokenValue(
				jakarta.servlet.http.HttpServletRequest request,
				CsrfToken csrfToken) {
			CsrfTokenRequestAttributeHandler handler = StringUtils.hasText(
					request.getHeader(csrfToken.getHeaderName()))
							? this.plainHandler
							: this.xorHandler;
			return handler.resolveCsrfTokenValue(request, csrfToken);
		}
	}
}
