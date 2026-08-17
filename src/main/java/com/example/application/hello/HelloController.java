package com.example.application.hello;

import com.example.application.hello.dto.HelloResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.application.api.WebApiConfig.API_V1;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v{version}/hello", version = API_V1, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Hello", description = "Public REST and MCP adapter-parity example")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService service;

    @GetMapping
    @Operation(summary = "Get the example greeting",
            description = "Runs the same deterministic use case that is also exposed as the hello MCP tool.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint greeting",
                    content = @Content(schema = @Schema(implementation = HelloResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    public HelloResponse hello() {
        return service.hello();
    }
}
