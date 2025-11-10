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
import java.util.Map;

@RestController
@RequestMapping("/admin/reservas")
public class ReservaAdminController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaAdminController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    // ✅ Verifica si el usuario autenticado tiene rol de RECEPCIONISTA o ADMIN
    private boolean esRecepcionistaOAdmin(Usuario usuario) {
        int rolId = Math.toIntExact(usuario.getRol().getIdRol());
        return rolId == 2 || rolId == 1; // 1 = Admin, 2 = Recepcionista
    }

    // 📋 1. Listar todas las reservas
    @GetMapping("/listar")
    public ResponseEntity<?> listarTodas(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!esRecepcionistaOAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permisos para acceder a todas las reservas."));
        }

        List<Reserva> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }

    // 🔄 2. Cambiar estado manualmente (confirmar, pendiente, etc.)
    @PatchMapping("/{idReserva}/estado")
    public ResponseEntity<?> cambiarEstadoReserva(
            @PathVariable Integer idReserva,
            @RequestParam String nuevoEstado,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!esRecepcionistaOAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permisos para cambiar estados de reservas."));
        }

        // Validar estados permitidos
        List<String> estadosPermitidos = List.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA", "MODIFICADA", "RECHAZADA");
        String estadoUpper = nuevoEstado.toUpperCase();
        if (!estadosPermitidos.contains(estadoUpper)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado no válido. Permitidos: " + estadosPermitidos));
        }

        Reserva reserva = reservaService.cambiarEstadoReserva(idReserva, estadoUpper);
        return ResponseEntity.ok(reserva);
    }

    // ❌ 3. Cancelar reserva manualmente
    @DeleteMapping("/{idReserva}/cancelar")
    public ResponseEntity<?> cancelarReservaManual(
            @PathVariable Integer idReserva,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!esRecepcionistaOAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permisos para cancelar reservas."));
        }

        reservaService.cancelarReservaAdmin(idReserva);
        return ResponseEntity.ok(Map.of("mensaje", "Reserva cancelada correctamente por recepción/admin."));
    }

    // ✅ 4. Marcar como completada manualmente
    @PatchMapping("/{idReserva}/completar")
    public ResponseEntity<?> completarReservaManual(
            @PathVariable Integer idReserva,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!esRecepcionistaOAdmin(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permisos para completar reservas."));
        }

        Reserva reserva = reservaService.marcarComoCompletada(idReserva);
        return ResponseEntity.ok(reserva);
    }
}

