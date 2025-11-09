package reservas.peluqueria.reservas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservas.peluqueria.reservas.security.TokenService;
import reservas.peluqueria.reservas.dto.DatosAutenticacion;
import reservas.peluqueria.reservas.dto.DatosRegistroUsuario;
import reservas.peluqueria.reservas.dto.DatosTokenJWT;
import reservas.peluqueria.reservas.entity.Rol;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.RolRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<DatosTokenJWT> login(@RequestBody DatosAutenticacion datos) {
        var authToken = new UsernamePasswordAuthenticationToken(datos.correoElectronico(), datos.contrasena());
        var authResult = authenticationManager.authenticate(authToken);

        var usuario = (Usuario) authResult.getPrincipal();
        var jwt = tokenService.generarToken(usuario);

        return ResponseEntity.ok(new DatosTokenJWT(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DatosRegistroUsuario datos) {
        if (usuarioRepository.findByCorreoElectronico(datos.correoElectronico()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está en uso");
        }

        // Buscar el rol CLIENTE (id 4)
        Rol rolCliente = rolRepository.findById(4)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre());
        usuario.setApellido(datos.apellido());
        usuario.setCorreoElectronico(datos.correoElectronico());
        usuario.setContrasenaHash(passwordEncoder.encode(datos.contrasena()));
        usuario.setRol(rolCliente);

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado exitosamente como CLIENTE");
    }
}