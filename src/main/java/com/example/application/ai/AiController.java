package com.example.application.ai;

import com.example.application.ai.dto.GenerateRequest;
import com.example.application.ai.dto.GenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/ai", version = API_V1)
@Tag(name = "AI")
public class AiController {
    private final AiService ai;

    public AiController(AiService ai) {
        this.ai = ai;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate text through the explicitly configured chat provider")
    public GenerateResponse generate(@Valid @RequestBody GenerateRequest request) {
        return ai.generate(request.prompt());
    }
}
