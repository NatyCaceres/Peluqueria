package reservas.peluqueria.reservas.controller;

import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.entity.Rol;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.RolRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/trabajadores")
public class TrabajadorController {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public TrabajadorController(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    // Crear trabajador
    @PostMapping("/crear")
    public Usuario crearTrabajador(@RequestBody Usuario usuario) {
        Rol rolTrabajador = rolRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Rol trabajador no encontrado"));
        usuario.setRol(rolTrabajador);
        return usuarioRepository.save(usuario);
    }

    // Listar todos los trabajadores
    @GetMapping("/listar")
    public List<Usuario> listarTrabajadores() {
        Rol rolTrabajador = rolRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Rol trabajador no encontrado"));
        return usuarioRepository.findByIdRol(Math.toIntExact(rolTrabajador.getIdRol()));
    }
}
