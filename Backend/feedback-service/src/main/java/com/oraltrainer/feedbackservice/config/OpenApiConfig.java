package com.oraltrainer.feedbackservice.config;

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
    public OpenAPI feedbackServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrainer - Feedback Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la génération des retours pédagogiques et des recommandations personnalisées. "
                                + "Il consolide les résultats d'analyse pour produire des axes d'amélioration constructifs et des exercices ciblés.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Serveur local - feedback-service")));
    }
}
