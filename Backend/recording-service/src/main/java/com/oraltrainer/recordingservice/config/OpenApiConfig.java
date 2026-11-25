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
                        .title("OralTrain - Recording Service API")
                        .version("2.0.0")
                        .description("Point d'entrée des productions orales : enregistrement et stockage audio (MongoDB GridFS), transcription (Whisper / Vosk) "
                                + "et analyse vocale prosodique (débit, pauses, hauteur, prononciation, fluidité, intonation) via speech-engine, avec relance après échec.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Serveur local - recording-service")));
    }
}
