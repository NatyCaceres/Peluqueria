package reservas.peluqueria.reservas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.dto.DatosHistorialReserva;
import reservas.peluqueria.reservas.dto.DatosReserva;
import reservas.peluqueria.reservas.entity.Reserva;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.UsuarioRepository;
import reservas.peluqueria.reservas.service.ReservaService;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaService reservaService,
                             UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/crear")
    public ResponseEntity<Reserva> crearReserva(@RequestBody DatosReserva datos,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        // El subject del token es el CORREO
        String correo = userDetails.getUsername();
        Usuario cliente = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer idCliente = cliente.getIdUsuario();
        Reserva reserva = reservaService.crearReserva(datos, idCliente);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> listarReservasCliente(@AuthenticationPrincipal UserDetails userDetails) {
        String correo = userDetails.getUsername();
        Usuario cliente = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer idCliente = cliente.getIdUsuario();

        return ResponseEntity.ok(reservaService.listarReservasCliente(idCliente));
    }

    @GetMapping("/trabajador/{idTrabajador}")
    public ResponseEntity<List<Reserva>> listarReservasTrabajador(@PathVariable Integer idTrabajador) {
        return ResponseEntity.ok(reservaService.listarReservasTrabajador(idTrabajador));
    }

    @GetMapping("/historial/{idCliente}")
    public ResponseEntity<List<DatosHistorialReserva>> historialCliente(@PathVariable Integer idCliente) {
        List<DatosHistorialReserva> historial = reservaService.listarHistorialCliente(idCliente);
        return ResponseEntity.ok(historial);
    }

    @PutMapping("/{idReserva}/modificar")
    public ResponseEntity<Reserva> modificarReserva(@PathVariable Integer idReserva,
                                                    @RequestBody DatosReserva datos,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Usuario cliente = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ Validar rol CLIENTE (id = 4)
        if (cliente.getRol().getIdRol() != 4) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Reserva reservaActualizada = reservaService.modificarReserva(idReserva, cliente.getIdUsuario(), datos);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{idReserva}/cancelar")
    public ResponseEntity<String> cancelarReserva(@PathVariable Integer idReserva,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Usuario cliente = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ Validar rol CLIENTE (id = 4)
        if (cliente.getRol().getIdRol() != 4) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo los clientes pueden cancelar sus reservas.");
        }

        reservaService.cancelarReserva(idReserva, cliente.getIdUsuario());
        return ResponseEntity.ok("Reserva cancelada correctamente.");
    }

    @PatchMapping("/{idReserva}/completar")
    public ResponseEntity<Reserva> completarReserva(@PathVariable Integer idReserva,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Usuario trabajador = usuarioRepository.findByCorreoElectronico(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ✅ Verificamos que tenga rol de TRABAJADOR (id_rol = 3)
        if (trabajador.getRol().getIdRol() != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Reserva reservaCompletada = reservaService.completarReserva(idReserva, trabajador.getIdUsuario());
        return ResponseEntity.ok(reservaCompletada);
    }
}
