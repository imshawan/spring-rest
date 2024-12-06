package com.imshawan.rest.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

  @Value("${openapi.dev-host-url}")
  private String devHostUrl;

  @Value("${openapi.dev-host-url}")
  private String prodHostUrl;

  @Bean
  public OpenAPI setupOpenAPIConfig() {
    String hostUrl = prodHostUrl.isEmpty() ? devHostUrl : prodHostUrl;
    Server server = new Server();
    server.setUrl(hostUrl);
    server.setDescription("Server URL");

    Contact contact = new Contact();
    contact.setEmail("contact@imshawan.dev");
    contact.setName("Shawan Mandal");
    contact.setUrl("https://www.imshawan.dev");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("Spring REST API Service")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage users onboarding and validation.").termsOfService("#")
        .license(mitLicense);

    return new OpenAPI().info(info).servers(List.of(server));
  }
}