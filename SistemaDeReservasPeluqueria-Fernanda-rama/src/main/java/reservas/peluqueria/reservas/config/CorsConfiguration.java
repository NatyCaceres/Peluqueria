package reservas.peluqueria.reservas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//el cors es un nuevo cors global (es decir, se cambio el codigo completo)

@Configuration
public class CorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // Usa allowedOriginPatterns (NO "*")
                        .allowedOriginPatterns(
                                "http://localhost:5500",
                                "http://127.0.0.1:5500"
                        )
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization") // por si lees el header en front
                        .allowCredentials(true);
            }
        };
    }
}

