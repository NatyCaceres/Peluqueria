package reservas.peluqueria.reservas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservas.peluqueria.reservas.dto.DatosHistorialReserva;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.ReservaRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.ReservaService;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/agenda")
public class AgendaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AgendaController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    // 1️⃣ Todas las reservas del día actual
    @GetMapping("/hoy")
    public ResponseEntity<?> getReservasHoy() {
        LocalDate hoy = LocalDate.now();
        return ResponseEntity.ok(reservaService.listarPorFecha(hoy));
    }

    // 2️⃣ Reservas por fecha específica
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> getReservasPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaParseada = LocalDate.parse(fecha);
            return ResponseEntity.ok(reservaService.listarPorFecha(fechaParseada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido (usa yyyy-MM-dd)");
        }
    }

    // 3️⃣ Reservas por trabajador y fecha
    @GetMapping("/trabajador/{idTrabajador}/fecha/{fecha}")
    public ResponseEntity<?> getReservasPorTrabajadorYFecha(@PathVariable Integer idTrabajador,
                                                            @PathVariable String fecha) {
        try {
            LocalDate fechaParseada = LocalDate.parse(fecha);
            return ResponseEntity.ok(reservaService.listarPorTrabajadorYFecha(idTrabajador, fechaParseada));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener reservas: " + e.getMessage());
        }
    }

    @GetMapping("/historial/{idCliente}")
    public ResponseEntity<List<DatosHistorialReserva>> historialCliente(@PathVariable Integer idCliente) {
        List<DatosHistorialReserva> historial = reservaService.listarHistorialCliente(idCliente);
        return ResponseEntity.ok(historial);
    }

}
