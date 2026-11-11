package com.oraltrainer.recordingservice.config;

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
    public OpenAPI recordingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrainer - Recording Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion des enregistrements audio et de leurs transcriptions au sein d'OralTrainer. "
                                + "Il orchestre la réception des flux vocaux, le suivi des métadonnées audio et la mise à disposition des transcriptions.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Serveur local - recording-service")));
    }
}
