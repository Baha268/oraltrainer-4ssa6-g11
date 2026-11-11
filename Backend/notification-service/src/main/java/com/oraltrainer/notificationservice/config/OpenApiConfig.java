package com.oraltrainer.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrainer - Notification Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion des notifications et des alertes utilisateurs au sein d'OralTrainer. "
                                + "Il assure la diffusion des rappels d'entraînement, des alertes de disponibilité des résultats et des messages système.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8088")
                                .description("Serveur local - notification-service")));
    }
}
