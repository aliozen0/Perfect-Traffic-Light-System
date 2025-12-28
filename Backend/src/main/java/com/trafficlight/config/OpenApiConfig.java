package com.trafficlight.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration with JWT Support
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * 
 * SPRINT 4: Added JWT Bearer Authentication
 */
@Configuration
public class OpenApiConfig {
    
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    
    @Bean
    public OpenAPI trafficLightOpenAPI() {
        // Server configuration
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");
        
        Server dockerServer = new Server();
        dockerServer.setUrl("http://localhost:8080");
        dockerServer.setDescription("Docker Environment");
        
        // Contact information
        Contact contact = new Contact();
        contact.setName("Traffic Light System Team - Team 10");
        contact.setEmail("support@trafficlight.com");
        
        // License
        License license = new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");
        
        // API Info
        Info info = new Info()
            .title("Traffic Light Management System API")
            .version("1.0.0")
            .description(
                "Comprehensive API for managing traffic light intersections, including:\n\n" +
                "- **Authentication**: JWT-based user authentication and authorization\n" +
                "- **Configuration**: Traffic light intersection configuration\n" +
                "- **Metrics**: Real-time traffic metrics and monitoring\n" +
                "- **Phase Management**: Traffic signal phase control\n\n" +
                "**Authentication Instructions:**\n" +
                "1. Use `/api/auth/login` or `/api/auth/register` to get a JWT token\n" +
                "2. Click the 'Authorize' button (🔓) at the top right\n" +
                "3. Enter your token in the format: `your-jwt-token-here`\n" +
                "4. Click 'Authorize' and then 'Close'\n" +
                "5. All protected endpoints will now include the token automatically\n\n" +
                "**Default Credentials:**\n" +
                "- Admin: `admin` / `admin123`\n" +
                "- User: `user` / `user123`"
            )
            .contact(contact)
            .license(license);
        
        // Security scheme - JWT Bearer Token
        SecurityScheme securityScheme = new SecurityScheme()
            .name(SECURITY_SCHEME_NAME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .description("Enter JWT token obtained from /api/auth/login or /api/auth/register");
        
        // Security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList(SECURITY_SCHEME_NAME);
        
        return new OpenAPI()
            .info(info)
            .servers(List.of(localServer, dockerServer))
            .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme))
            .addSecurityItem(securityRequirement);
    }
}