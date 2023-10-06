package org.example.conf;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Сервис по управлению погодой",
                description = "Домашнее задание №3", version = "1.0.0",
                contact = @Contact(
                        name = "Sadovnikov Daniil"
                )
        )
)
public class AppConfig {

}