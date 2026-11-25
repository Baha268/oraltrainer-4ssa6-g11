package com.oraltrainer.simulationservice.config;

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
    public OpenAPI simulationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrain - Simulation Service API")
                        .version("2.0.0")
                        .description("Orchestration des examens blancs en trois étapes (PREPARATION, MONOLOGUE, INTERACTION), "
                                + "gestion des relances de l'examinateur et calcul de la note estimée une fois les feedbacks reçus.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("Serveur local - simulation-service")));
    }
}
