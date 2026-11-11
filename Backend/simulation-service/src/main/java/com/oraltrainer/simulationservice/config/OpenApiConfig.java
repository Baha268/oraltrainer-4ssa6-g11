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
                        .title("OralTrainer - Simulation Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion des épreuves blanches chronométrées et des relances interactives. "
                                + "Il pilote le cycle de vie des sessions d'examen oral, les minuteries et l'enchaînement des questions de relance.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("Serveur local - simulation-service")));
    }
}
