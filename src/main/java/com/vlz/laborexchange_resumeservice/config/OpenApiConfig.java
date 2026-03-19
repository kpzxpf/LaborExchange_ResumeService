package com.vlz.laborexchange_resumeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                                Manages job seeker resumes and education records for LaborExchange.

                                **Access rules:**
                                - Only users with `JOB_SEEKER` role may create resumes (checked via UserService).
                                - Only the resume owner may update, delete, publish, or manage skills.

                                **Headers injected by API Gateway:**
                                - `X-User-Id` — authenticated user ID

                                **Elasticsearch indexing:** resume create/update/skills change publishes a `ResumeIndexEvent` to Kafka topic `indexing-resume`.
                                """)
                        .contact(new Contact().name("LaborExchange Team"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Resumes").description("Resume CRUD, skill management, and publish control"),
                        new Tag().name("Education").description("Education records linked to a resume")));
    }
}
