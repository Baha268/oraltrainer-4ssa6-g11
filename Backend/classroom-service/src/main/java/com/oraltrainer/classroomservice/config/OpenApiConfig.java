package com.oraltrainer.classroomservice.config;

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
    public OpenAPI classroomServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrain - Classroom Service API")
                        .version("2.0.0")
                        .description("Microservice dédié à l'organisation pédagogique collective : établissements, classes, inscriptions des apprenants, "
                                + "parcours assignés par le formateur, commentaires et tableaux de bord de classe et d'établissement.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8089")
                                .description("Serveur local - classroom-service")));
    }
}
