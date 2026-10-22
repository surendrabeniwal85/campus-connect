package com.placementassistant.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTests extends JwtTestPropertySupport {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void actuatorHealthEndpointIsPublic() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk());
	}

	@Test
	void applicationRoutesRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/v1/protected-test-route"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginIsPublicAndDoesNotRequireCsrf() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}
}
