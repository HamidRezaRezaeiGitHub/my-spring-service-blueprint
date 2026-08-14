package com.example.application.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.ai")
@Validated
public class AiProperties {
    @Min(1)
    @Max(20_000)
    private int maxPromptLength = 4_000;

    public int getMaxPromptLength() {
        return maxPromptLength;
    }

    public void setMaxPromptLength(int maxPromptLength) {
        this.maxPromptLength = maxPromptLength;
    }
}
