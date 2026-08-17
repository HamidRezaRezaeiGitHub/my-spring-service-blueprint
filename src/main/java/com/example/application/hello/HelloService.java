package com.example.application.hello;

import com.example.application.hello.dto.HelloResponse;
import org.springframework.stereotype.Service;

/**
 * Owns the hello use case shared by every inbound adapter.
 */
@Service
public class HelloService {
    public HelloResponse hello() {
        return new HelloResponse("Hello from the Spring service blueprint!");
    }
}
