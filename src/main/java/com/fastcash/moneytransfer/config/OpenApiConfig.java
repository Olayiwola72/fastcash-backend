package com.fastcash.moneytransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {   

	private final String bearerSecuritySchemeName;
	
	private final String basicSecuritySchemeName;
	
	public OpenApiConfig(
			@Value("${bearer.security.scheme.name}") String bearerSecuritySchemeName,
			@Value("${basic.security.scheme.name}") String basicSecuritySchemeName
			) {
		this.bearerSecuritySchemeName = bearerSecuritySchemeName;
		this.basicSecuritySchemeName = basicSecuritySchemeName;
	}
    
	@Bean
    public OpenAPI baseOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Money Transfer Swagger Project OpenAPI Docs")
                        .version("1.0.0")
                        .description("OpenAPI Docs Description")
                )
                .addSecurityItem(new SecurityRequirement().addList(this.bearerSecuritySchemeName).addList(this.basicSecuritySchemeName))
                .components(new Components()
                        .addSecuritySchemes(this.bearerSecuritySchemeName,
                                new SecurityScheme()
                                        .name(this.bearerSecuritySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSecuritySchemes(this.basicSecuritySchemeName,
                                new SecurityScheme()
                                        .name(this.basicSecuritySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                        )
                );
    }
	
}