package com.example.application.ai;

import com.example.application.ai.dto.GenerateResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final @Nullable ChatClient chatClient;
    private final AiProperties properties;

    @Autowired
    public AiService(ObjectProvider<ChatClient.Builder> chatClientBuilder, AiProperties properties, @Value("${spring.ai.model.chat:none}") String configuredChatModel) {
        ChatClient.Builder builder = "none".equalsIgnoreCase(configuredChatModel)
                ? null
                : chatClientBuilder.getIfAvailable();
        this.chatClient = builder == null ? null : builder.build();
        this.properties = properties;
    }

    AiService(@Nullable ChatClient chatClient, AiProperties properties) {
        this.chatClient = chatClient;
        this.properties = properties;
    }

    public GenerateResponse generate(@Nullable String prompt) {
        String normalized = prompt == null ? "" : prompt.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Prompt must not be blank");
        }
        if (normalized.length() > properties.getMaxPromptLength()) {
            throw new IllegalArgumentException("Prompt exceeds configured maximum length");
        }
        if (chatClient == null) {
            throw new AiUnavailableException("AI generation is disabled");
        }
        try {
            String content = chatClient.prompt().user(normalized).call().content();
            if (content == null || content.isBlank()) {
                throw new AiUnavailableException("AI provider returned an empty response");
            }
            return new GenerateResponse(content);
        } catch (AiUnavailableException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AiUnavailableException("AI provider is temporarily unavailable", exception);
        }
    }
}
