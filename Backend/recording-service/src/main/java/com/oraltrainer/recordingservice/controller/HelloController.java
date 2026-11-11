package com.oraltrainer.recordingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/recording")
@Tag(name = "Recording Service Hello", description = "Endpoint de test et de vérification de disponibilité pour recording-service")
public class HelloController {

    private static final String SERVICE_NAME = "recording-service";

    @GetMapping("/hello")
    @Operation(
            summary = "Hello recording-service",
            description = "Retourne un message de bienvenue, le nom du service, son statut et l'horodatage courant."
    )
    @ApiResponse(responseCode = "200", description = "Réponse retournée avec succès")
    public ResponseEntity<Map<String, Object>> sayHello() {
        return ResponseEntity.ok(Map.of(
                "message", "Hello from " + SERVICE_NAME + "!",
                "service", SERVICE_NAME,
                "status", "UP",
                "timestamp", Instant.now()
        ));
    }
}
