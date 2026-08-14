package com.example.application.hello;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloToolRegistrationTest {

    @Test
    void toolProvider_shouldDiscoverHello_whenAdapterIsRegistered() {
        // Arrange
        var tools = new HelloMcpTools(new HelloService());
        var provider = MethodToolCallbackProvider.builder().toolObjects(tools).build();

        // Act
        var callbacks = provider.getToolCallbacks();

        // Assert
        assertEquals(1, callbacks.length);
        assertEquals("hello", callbacks[0].getToolDefinition().name());
    }
}
