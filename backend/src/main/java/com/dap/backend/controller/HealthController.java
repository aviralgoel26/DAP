package com.dap.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Lightweight health-check controller for external uptime monitoring (e.g. Render Free).
 * <p>
 * This endpoint does not require authentication, does not query MongoDB or GridFS,
 * and performs no heavy computation.
 * </p>
 */
@Tag(name = "Health API", description = "Lightweight health check for uptime monitoring")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Map<String, String> HEALTH_STATUS = Map.of(
            "status", "UP",
            "service", "DAP Backend"
    );

    @Operation(summary = "Check backend health status", description = "Returns 200 OK with service health details")
    @GetMapping
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(HEALTH_STATUS);
    }
}
