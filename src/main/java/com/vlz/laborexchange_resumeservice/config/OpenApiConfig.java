package com.vlz.laborexchange_resumeservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resume Service API")
                        .version("1.0.0")
                        .description("""
                                Manages job seeker resumes and education records.

                                **Access rules:**
                                - Only `JOB_SEEKER` users may create resumes (checked via UserService).
                                - Only the resume owner may update, delete, publish, or manage skills.

                                **Headers injected by API Gateway:**
                                - `X-User-Id` — authenticated user ID

                                **Elasticsearch indexing:** create / update / skill change publishes a \
`ResumeIndexEvent` to Kafka topic `indexing-resume`.

                                **Database:** PostgreSQL (`resumedb`, port 5436).
                                """)
                        .license(new License().name("MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained via POST /api/auth/login")))
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Resumes").description("Resume CRUD, skill management, and publish control"),
                        new Tag().name("Education").description("Education records linked to a resume")));
    }
}
