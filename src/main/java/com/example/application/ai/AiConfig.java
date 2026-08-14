package com.example.application.ai;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AiProperties.class)
public class AiConfig {
    @Bean
    ToolCallbackProvider aiToolCallbackProvider(AiMcpTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
