package com.oraltrainer.progressservice.config;

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
    public OpenAPI progressServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrain - Progress Service API")
                        .version("2.0.0")
                        .description("Carnet de suivi de l'apprenant : historique par compétence et niveau CECRL, tendances, objectifs, "
                                + "alertes de progression (stagnation, baisse, inactivité), délivrance d'attestations et recommandations d'exercices.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8086")
                                .description("Serveur local - progress-service")));
    }
}
