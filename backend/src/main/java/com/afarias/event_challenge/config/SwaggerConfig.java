package com.afarias.event_challenge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
        @Info(
                title = "Api de gestion de eventos",
                version = "1.0",
                description = "Documentación de la API para gestionar eventos."))
public class SwaggerConfig {
}
