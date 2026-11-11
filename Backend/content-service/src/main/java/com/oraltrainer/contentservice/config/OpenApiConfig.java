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
                        .title("OralTrainer - Content Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion du référentiel pédagogique, des cours, des modules et des exercices d'entraînement oral. "
                                + "Il structure les parcours d'apprentissage, les sujets d'examen et les ressources pédagogiques.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Serveur local - content-service")));
    }
}
