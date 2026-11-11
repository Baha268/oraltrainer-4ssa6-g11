package com.oraltrainer.userservice.controller;

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
@RequestMapping("/api/user")
@Tag(name = "User Service Hello", description = "Endpoint de test et de vérification de disponibilité pour user-service")
public class HelloController {

    private static final String SERVICE_NAME = "user-service";

    @GetMapping("/hello")
    @Operation(
            summary = "Hello user-service",
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
