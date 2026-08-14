package com.example.application.hello;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class HelloMcpConfig {
    @Bean
    ToolCallbackProvider helloToolCallbackProvider(HelloMcpTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
