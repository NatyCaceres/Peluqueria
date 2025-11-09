package reservas.peluqueria.reservas.springdoc;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//para tener visible en http://localhost:8080/swagger-ui/ el token para registrarnos
public class SpringDocConfiguration {
    //sacado de la documentacion. 13.34

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                //nuevas cosas para que se muestren el el swagg
                .info(new Info()
                        .title("Peluqueria")
                        .description("API Rest de la aplicación peluqueria")
                        .contact(new Contact()
                                .name("Estudiante informatica")
                                .email("Sin contacto"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("")));
    }
}
