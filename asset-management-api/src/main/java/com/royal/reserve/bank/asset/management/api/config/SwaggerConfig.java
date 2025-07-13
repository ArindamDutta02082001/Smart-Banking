package com.royal.reserve.bank.asset.management.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Royal Reserve")
                        .version("1.0.0")
                        .description("This documents microservices for Account apis")
                        .contact(new Contact()
                                .name("Arindam Dutta")
                                .email("arindamdutta02082001@gmail.com")
                                .url(""))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
