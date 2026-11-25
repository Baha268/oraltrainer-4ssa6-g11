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
                        .title("OralTrain - User Service API")
                        .version("2.0.0")
                        .description("Service commun dédié à la gestion des profils utilisateurs, des rôles applicatifs (APPRENANT, FORMATEUR, ADMIN_ETABLISSEMENT, ADMIN) "
                                + "et du lien avec l'identité Keycloak (keycloakId) ; publie l'événement user.registered.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Serveur local - user-service")));
    }
}
