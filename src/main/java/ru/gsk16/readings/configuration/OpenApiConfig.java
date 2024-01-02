package ru.gsk16.readings.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "gsk-16-readings API",
                description = "Сервис отправки ежемесячных показаний эл.счетчиков", version = "1.0.0",
                contact = @Contact(
                        name = "Sergey Poletaev",
                        email = "info.elekit@.yandex.ru"
                )
        )
)
public class OpenApiConfig {
}
