package reservas.peluqueria.reservas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import reservas.peluqueria.reservas.security.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // Habilita CORS
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/auth/**").permitAll()
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                            // 🧾 CONSULTAS PÚBLICAS (ver servicios o trabajadores)
                            .requestMatchers(HttpMethod.GET,
                                    "/servicios/**",
                                    "/trabajadores-servicios/**"
                            ).permitAll()

                            // 👤 CLIENTE
                            .requestMatchers("/reservas/**").permitAll()

                            // 🧰 TRABAJADOR
                            .requestMatchers("/trabajador/**").permitAll()

                            // 🏢 RECEPCIONISTA / ADMINISTRADOR
                            .requestMatchers("/admin/**").permitAll()

                            // 🔒 CUALQUIER OTRO REQUIERE AUTENTICACIÓN
                            .anyRequest().authenticated();
                })
                //luego de terminar de desarrollar debo colocar este bloque
                // .authorizeHttpRequests(req -> {
                //    req.requestMatchers("/auth/**").permitAll()
                //       .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                //       .anyRequest().authenticated();
                //})
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)//por defecto hace primero el filtro de spring pero se necesita hacer el otro para autenticar y luego este
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}