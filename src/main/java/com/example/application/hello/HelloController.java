package com.example.application.hello;

import com.example.application.hello.dto.HelloResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.application.api.WebApiConfig.API_V1;

@RestController
@RequestMapping(path = "/api/v{version}/hello", version = API_V1)
@Tag(name = "Hello")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService service;

    @GetMapping
    @Operation(summary = "Return the blueprint greeting")
    public HelloResponse hello() {
        return service.hello();
    }
}
