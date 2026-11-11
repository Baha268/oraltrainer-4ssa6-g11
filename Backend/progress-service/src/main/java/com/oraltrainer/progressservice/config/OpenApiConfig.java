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
                        .title("OralTrainer - Progress Service API")
                        .version("1.0.0")
                        .description("Microservice dédié au suivi de progression des apprenants, à l'alimentation des tableaux de bord et à la délivrance des attestations. "
                                + "Il agrège les indicateurs de maîtrise orale et les jalons atteints au fil des entraînements.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8086")
                                .description("Serveur local - progress-service")));
    }
}
