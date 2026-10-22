package com.placementassistant.backend.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTests {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(new ErrorTestController())
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void validationErrorReturns400WithFiveFieldEnvelope() throws Exception {
		mockMvc.perform(post("/validation")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.message").value("One or more fields are invalid."))
				.andExpect(jsonPath("$.path").value("/validation"))
				.andExpect(jsonPath("$.errors").doesNotExist())
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void malformedJsonReturns400WithSafeMessage() throws Exception {
		mockMvc.perform(post("/validation")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{not-json"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
				.andExpect(jsonPath("$.message").value("The request body is invalid."))
				.andExpect(jsonPath("$.path").value("/validation"))
				.andExpect(jsonPath("$.errors").doesNotExist())
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void unexpectedExceptionReturns500WithoutInternalDetails() throws Exception {
		String responseBody = mockMvc.perform(get("/unexpected"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.status").value(500))
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(jsonPath("$.message").value("An unexpected error occurred."))
				.andExpect(jsonPath("$.path").value("/unexpected"))
				.andExpect(jsonPath("$.errors").doesNotExist())
				.andExpect(jsonPath("$.*", hasSize(5)))
				.andReturn()
				.getResponse()
				.getContentAsString();
		assertThat(responseBody)
				.doesNotContain("sensitive SQL", "password", "token", "IllegalStateException");
	}

	@RestController
	public static class ErrorTestController {

		@PostMapping("/validation")
		public String validate(@Valid @RequestBody ValidationRequest request) {
			return "accepted";
		}

		@GetMapping("/unexpected")
		public String fail() {
			throw new IllegalStateException("sensitive SQL details, password, and token");
		}
	}

	public record ValidationRequest(@NotBlank String name) {
	}
}
