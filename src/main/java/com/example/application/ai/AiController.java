package com.example.application.ai;

import com.example.application.ai.dto.GenerateRequest;
import com.example.application.ai.dto.GenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.application.api.WebApiConfig.API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v{version}/ai", version = API_V1, produces = APPLICATION_JSON_VALUE)
@Tag(name = "AI", description = "Optional outbound chat-model integration")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @PostMapping(path = "/generate", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate text",
            description = "Sends a bounded prompt to the explicitly configured chat provider.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Generated text",
                    content = @Content(schema = @Schema(implementation = GenerateResponse.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "415", ref = "#/components/responses/UnsupportedMediaType"),
            @ApiResponse(responseCode = "503", ref = "#/components/responses/ServiceUnavailable"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    public GenerateResponse generate(@Valid @RequestBody GenerateRequest request) {
        return aiService.generate(request.prompt());
    }
}
