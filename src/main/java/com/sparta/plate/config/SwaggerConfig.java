package com.sparta.plate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		String jwt = "JWT";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
		Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
			.name(jwt)
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
		);
		return new OpenAPI()
			.components(new Components())
			.info(apiInfo())
			.addSecurityItem(securityRequirement)
			.components(components);
	}
	private Info apiInfo() {
		return new Info()
			.title("[4조 Plate] API 문서 ")
			.description("[4조 Plate] API 명세에 대한 페이지입니다.")
			.version("0.0.1"); // API의 버전
	}

}