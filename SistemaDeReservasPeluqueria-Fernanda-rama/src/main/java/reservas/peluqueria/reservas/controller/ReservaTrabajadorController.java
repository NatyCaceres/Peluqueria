package reservas.peluqueria.reservas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.ReservaService;

import java.util.List;

@RestController
@RequestMapping("/trabajador/reservas")
public class ReservaTrabajadorController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaTrabajadorController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ 1. Listar reservas asignadas al trabajador autenticado
    @GetMapping
    public ResponseEntity<List<Reserva>> listarReservasDelTrabajador(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario trabajador = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (trabajador.getRol().getIdRol() != 3) { // 3 = TRABAJADOR
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Reserva> reservas = reservaService.listarReservasTrabajador(trabajador.getIdUsuario());
        return ResponseEntity.ok(reservas);
    }

    // ✅ 2. Marcar una reserva como completada
    @PatchMapping("/{idReserva}/completar")
    public ResponseEntity<?> completarReserva(
            @PathVariable Integer idReserva,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario trabajador = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (trabajador.getRol().getIdRol() != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo los trabajadores pueden completar reservas.");
        }

        Reserva reserva = reservaService.completarReserva(idReserva, trabajador.getIdUsuario());
        return ResponseEntity.ok(reserva);
    }

    // ✅ 3. Rechazar o cancelar una reserva pendiente (por el trabajador)
    @PatchMapping("/{idReserva}/rechazar")
    public ResponseEntity<?> rechazarReserva(
            @PathVariable Integer idReserva,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario trabajador = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (trabajador.getRol().getIdRol() != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo los trabajadores pueden rechazar reservas.");
        }

        reservaService.cancelarReservaPorTrabajador(idReserva, trabajador.getIdUsuario());
        return ResponseEntity.ok("Reserva rechazada correctamente.");
    }
}