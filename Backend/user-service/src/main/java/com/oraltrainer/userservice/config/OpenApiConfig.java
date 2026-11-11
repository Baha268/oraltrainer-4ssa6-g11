package com.oraltrainer.userservice.config;

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
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrainer - User Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion des utilisateurs, profils, rôles et groupes au sein de la plateforme OralTrainer. "
                                + "Il centralise l'identité des apprenants et encadrants, la gestion des autorisations et l'organisation des cohortes.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Serveur local - user-service")));
    }
}
