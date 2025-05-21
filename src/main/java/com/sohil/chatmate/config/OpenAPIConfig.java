package com.sohil.chatmate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {


    @Value("${springdoc.api-docs.title}")
    String apiDocTitle;

    @Value("${springdoc.api-docs.description}")
    String apiDocDescription;

    @Value("${springdoc.api-docs.version}")
    String apiDocVersion;

    @Bean
    public OpenAPI openAPIConfiguration() {
        return new OpenAPI().info(
                new Info()
                        .description(apiDocDescription)
                        .title(apiDocTitle)
                        .version(apiDocVersion)
                        .contact(new Contact().email("sohilgoud@gmail.com").name("Babburi Sohil Goud"))
        );
    }
}
