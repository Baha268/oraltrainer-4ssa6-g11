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
                        .title("OralTrain - Feedback Service API")
                        .version("2.0.0")
                        .description("Production du retour pédagogique après l'analyse d'un enregistrement : corrections (grammaire, lexique, cohérence, reformulations), "
                                + "scores CECRL par critère, recommandations et validation ou ajustement par le formateur.")
                        .contact(new Contact()
                                .name("OralTrain Team (4SSA6-G1)")
                                .email("contact@oraltrain.local")
                                .url("https://github.com/Baha268/oraltrainer-4ssa6-g11")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Serveur local - feedback-service")));
    }
}
