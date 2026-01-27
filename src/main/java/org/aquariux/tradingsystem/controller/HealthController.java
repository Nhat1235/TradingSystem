package org.aquariux.tradingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aquariux.tradingsystem.common.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Check if the application is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = HealthResponse.class)))
    })
    public ResponseEntity<org.aquariux.tradingsystem.common.ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("application", "Trading System");
        healthData.put("version", "1.0.0");
        
        return ResponseEntity.ok(org.aquariux.tradingsystem.common.ApiResponse.success("Application is running", healthData));
    }
    
    @Schema(description = "Health check response")
    private static class HealthResponse {
        @Schema(description = "Success status", example = "true")
        public boolean success;
        
        @Schema(description = "Response message", example = "Application is running")
        public String message;
        
        @Schema(description = "Health data")
        public HealthData data;
        
        @Schema(description = "Timestamp", example = "2026-01-27T10:00:00")
        public LocalDateTime timestamp;
    }
    
    @Schema(description = "Health data details")
    private static class HealthData {
        @Schema(description = "Application status", example = "UP")
        public String status;
        
        @Schema(description = "Current timestamp", example = "2026-01-27T10:00:00")
        public LocalDateTime timestamp;
        
        @Schema(description = "Application name", example = "Trading System")
        public String application;
        
        @Schema(description = "Application version", example = "1.0.0")
        public String version;
    }
}
