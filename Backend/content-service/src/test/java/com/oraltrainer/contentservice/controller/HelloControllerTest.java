package com.oraltrainer.contentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHelloResponse() throws Exception {
        mockMvc.perform(get("/api/content/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from content-service!"))
                .andExpect(jsonPath("$.service").value("content-service"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void shouldExposeOpenApiDocsAndSwaggerUi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("OralTrainer - Content Service API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.info.description", containsString("référentiel pédagogique")))
                .andExpect(jsonPath("$.paths['/api/content/hello'].get").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
