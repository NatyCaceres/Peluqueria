package reservas.peluqueria.reservas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.entity.HorariosDisponibles;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.HorariosDisponiblesRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.HorariosDisponiblesService;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/horarios-disponibles")
public class HorariosDisponiblesController {

    private final HorariosDisponiblesService horarioService;
    private final UsuarioRepository usuarioRepository;

    public HorariosDisponiblesController(HorariosDisponiblesService horarioService, UsuarioRepository usuarioRepository) {
        this.horarioService = horarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // 🟢 Listar todos (solo admin o recepcionista)
    @GetMapping("/todos")
    public ResponseEntity<?> listarTodos(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol().getIdRol() == 1 || usuario.getRol().getIdRol() == 2) {
            return ResponseEntity.ok(horarioService.listarTodos());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para ver todos los horarios");
    }

    // 🟢 Listar por trabajador (disponible para todos)
    @GetMapping("/trabajador/{idTrabajador}")
    public List<HorariosDisponibles> getHorariosPorTrabajador(@PathVariable Integer idTrabajador) {
        return horarioService.listarPorTrabajador(idTrabajador);
    }

    // 🟢 Crear horario (solo trabajador, recepcionista o admin)
    @PostMapping("/crear")
    public ResponseEntity<?> crearHorario(
            @RequestBody HorariosDisponibles horario,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long rol = usuario.getRol().getIdRol();

        // Solo trabajador, recepcionista o admin
        if (rol == 1 || rol == 2 || rol == 3) {
            if (rol == 3) {
                // si es trabajador, se fuerza su propio ID
                horario.setTrabajador(usuario);
            }
            return ResponseEntity.ok(horarioService.crear(horario));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para crear horarios");
    }

    // 🟠 Actualizar horario
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarHorario(
            @PathVariable Integer id,
            @RequestBody HorariosDisponibles horarioNuevo,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long rol = usuario.getRol().getIdRol();

        if (rol == 1 || rol == 2 || rol == 3) {
            return ResponseEntity.ok(horarioService.actualizar(id, horarioNuevo));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para actualizar horarios");
    }

    // 🔴 Eliminar horario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarHorario(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long rol = usuario.getRol().getIdRol();

        if (rol == 1 || rol == 2 || rol == 3) {
            horarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para eliminar horarios");
    }
}
