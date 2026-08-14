package com.example.application.ai;

import com.example.application.ai.dto.GenerateResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class AiMcpTools {
    private final AiService ai;

    public AiMcpTools(AiService ai) {
        this.ai = ai;
    }

    @Tool(name = "generate_text", description = "Generate text with the configured optional chat model")
    public GenerateResponse generate(@ToolParam(description = "Prompt, at most 4000 characters") String prompt) {
        return ai.generate(prompt);
    }
}
