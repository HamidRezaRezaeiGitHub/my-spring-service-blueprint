package com.example.application.hello;

import com.example.application.hello.dto.HelloResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * MCP inbound adapter; it contains no behavior beyond delegation.
 */
@Component
@RequiredArgsConstructor
public class HelloMcpTools {

    private final HelloService service;

    @Tool(name = "hello", description = "Return the service greeting")
    public HelloResponse hello() {
        return service.hello();
    }
}
