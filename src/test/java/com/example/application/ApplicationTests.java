package com.example.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void context_shouldLoad_whenOptionalProvidersAreDisabled() {
		// Arrange, act, and assert are performed by Spring Boot context initialization.
	}

}
