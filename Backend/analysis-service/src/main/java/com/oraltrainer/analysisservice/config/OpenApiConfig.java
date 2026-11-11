package com.oraltrainer.analysisservice.config;

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
    public OpenAPI analysisServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OralTrainer - Analysis Service API")
                        .version("1.0.0")
                        .description("Microservice dédié à l'analyse linguistique et acoustique des enregistrements vocaux. "
                                + "Il évalue la fluidité, le débit de parole, les pauses, l'intonation ainsi que la qualité lexicale et grammaticale.")
                        .contact(new Contact()
                                .name("OralTrainer Team (G11)")
                                .email("contact@oraltrainer.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8087")
                                .description("Serveur local - analysis-service")));
    }
}
