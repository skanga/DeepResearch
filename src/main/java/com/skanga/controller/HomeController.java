package com.skanga.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple controller for the welcome endpoint.
 * Returns a friendly JSON payload indicating the service is running.
 */
@RestController
public class HomeController {
    @GetMapping("/welcome")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "DeepResearch API is running. Use /api/research endpoints.");
        return response;
    }
}
