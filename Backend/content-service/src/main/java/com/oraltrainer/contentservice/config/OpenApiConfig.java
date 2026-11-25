package com.oraltrainer.contentservice.config;

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
    public OpenAPI contentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrain - Content Service API")
                        .version("2.0.0")
                        .description("Catalogue pédagogique central d'OralTrain (niveaux CECRL A1 à C2) : gestion des certifications, des sujets d'oral, "
                                + "des grilles d'évaluation CECRL pondérées et des exercices réutilisables.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Serveur local - content-service")));
    }
}
