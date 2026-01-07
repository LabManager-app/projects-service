package com.labmanager.projects.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI projectsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("LabManager - Projects Service API")
                .version("1.0")
                .description("API za upravljanje projektov in generiranje predlogov opreme (minimalna OpenAPI dokumentacija)."));
    }

}
