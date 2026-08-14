package com.example.application.hello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloAdaptersTest {

    @Test
    void adapters_shouldReturnEquivalentResults_whenInvoked() {
        // Arrange
        HelloService service = new HelloService();
        HelloController rest = new HelloController(service);
        HelloMcpTools mcp = new HelloMcpTools(service);

        // Act
        var restResult = rest.hello();
        var mcpResult = mcp.hello();

        // Assert
        assertEquals(service.hello(), restResult);
        assertEquals(restResult, mcpResult);
    }
}
