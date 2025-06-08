package com.educagestor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 * 
 * Configures API documentation with security schemes and server information.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configure OpenAPI documentation
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI educaGestorOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    /**
     * API information
     * 
     * @return API info
     */
    private Info apiInfo() {
        return new Info()
                .title("EducaGestor360 API")
                .description("Sistema integral de gestión educativa - API RESTful")
                .version("1.0.0")
                .contact(new Contact()
                        .name("EducaGestor360 Team")
                        .email("support@educagestor.com")
                        .url("https://educagestor.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Server list
     * 
     * @return list of servers
     */
    private List<Server> serverList() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort + "/api")
                .description("Local development server");

        Server productionServer = new Server()
                .url("https://api.educagestor.com")
                .description("Production server");

        return List.of(localServer, productionServer);
    }

    /**
     * Security requirement
     * 
     * @return security requirement
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }

    /**
     * Security components
     * 
     * @return security components
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", 
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication"));
    }
}
