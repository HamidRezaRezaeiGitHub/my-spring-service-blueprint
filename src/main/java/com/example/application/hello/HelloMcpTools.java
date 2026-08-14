package com.example.application.hello;

import com.example.application.hello.dto.HelloResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/** MCP inbound adapter; it contains no behavior beyond delegation. */
@Component
public class HelloMcpTools {
    private final HelloService helloService;

    public HelloMcpTools(HelloService helloService) {
        this.helloService = helloService;
    }

    @Tool(name = "hello", description = "Return the service greeting")
    public HelloResponse hello() {
        return helloService.hello();
    }
}
