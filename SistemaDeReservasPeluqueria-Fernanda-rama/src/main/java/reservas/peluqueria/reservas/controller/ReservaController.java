package reservas.peluqueria.reservas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
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
}
