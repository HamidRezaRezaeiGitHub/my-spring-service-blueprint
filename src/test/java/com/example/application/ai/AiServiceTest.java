package com.example.application.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiServiceTest {

    @Test
    void generate_shouldReturnContent_whenProviderResponds() {
        // Arrange
        ChatClient chat = chatClientReturning("Generated text");
        var service = new AiService(chat, properties(4000));

        // Act
        var response = service.generate("Write a sentence");

        // Assert
        assertEquals("Generated text", response.content());
    }

    @Test
    void generate_shouldReturnUnavailable_whenChatModelIsDisabled() {
        // Arrange
        var service = new AiService((ChatClient) null, properties(4000));

        // Act and assert
        assertThrows(AiUnavailableException.class, () -> service.generate("Write a sentence"));
    }

    @Test
    void generate_shouldRejectPrompt_whenConfiguredLimitIsExceeded() {
        // Arrange
        var service = new AiService(mock(ChatClient.class), properties(5));

        // Act and assert
        assertThrows(IllegalArgumentException.class, () -> service.generate("sixsix"));
    }

    @Test
    void generate_shouldReturnUnavailable_whenProviderReturnsEmptyContent() {
        // Arrange
        var service = new AiService(chatClientReturning("  "), properties(4000));

        // Act and assert
        assertThrows(AiUnavailableException.class, () -> service.generate("Write a sentence"));
    }

    @Test
    void generate_shouldHideProviderFailure_whenProviderThrows() {
        // Arrange
        ChatClient chat = mock(ChatClient.class);
        when(chat.prompt()).thenThrow(new RuntimeException("provider-secret-detail"));
        var service = new AiService(chat, properties(4000));

        // Act
        AiUnavailableException exception = assertThrows(AiUnavailableException.class,
                () -> service.generate("Write a sentence"));

        // Assert
        assertEquals("AI provider is temporarily unavailable", exception.getMessage());
    }

    private static ChatClient chatClientReturning(String content) {
        ChatClient chat = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec request = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec response = mock(ChatClient.CallResponseSpec.class);
        when(chat.prompt()).thenReturn(request);
        when(request.user("Write a sentence")).thenReturn(request);
        when(request.call()).thenReturn(response);
        when(response.content()).thenReturn(content);
        return chat;
    }

    private static AiProperties properties(int maximum) {
        AiProperties properties = new AiProperties();
        properties.setMaxPromptLength(maximum);
        return properties;
    }
}
