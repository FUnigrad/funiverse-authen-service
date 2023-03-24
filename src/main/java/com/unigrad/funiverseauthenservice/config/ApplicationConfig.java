package com.unigrad.funiverseauthenservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(new Info()
                .title("My API")
                .version("1.0.0")
                .description("API documentation using springdoc-openapi and OpenAPI 3."));
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}