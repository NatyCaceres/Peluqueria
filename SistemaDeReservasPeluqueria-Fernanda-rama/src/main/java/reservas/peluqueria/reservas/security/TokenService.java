package reservas.peluqueria.reservas.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reservas.peluqueria.reservas.entity.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String SECRET_KEY;
    private static final String EMISOR = "peluqueria";

    public String generarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer(EMISOR)
                .withSubject(usuario.getCorreoElectronico())
                .withClaim("rol", usuario.getRol().getNombreRol())
                .withExpiresAt(generarFechaExpiracion())
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String obtenerSubject(String tokenJWT) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .withIssuer(EMISOR)
                .build()
                .verify(tokenJWT)
                .getSubject();
    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
