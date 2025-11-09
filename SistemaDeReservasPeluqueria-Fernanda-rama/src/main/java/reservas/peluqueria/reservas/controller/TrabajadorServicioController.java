package reservas.peluqueria.reservas.controller;

import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.dto.DatosTrabajador;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.entity.TrabajadorServicio;
import reservas.peluqueria.reservas.entity.Usuario;
import reservas.peluqueria.reservas.repository.ServicioRepository;
import reservas.peluqueria.reservas.repository.TrabajadoresServiciosRepository;
import reservas.peluqueria.reservas.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/trabajadores-servicios")
public class TrabajadorServicioController {

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final TrabajadoresServiciosRepository tsRepository;

    public TrabajadorServicioController(UsuarioRepository usuarioRepository,
                                        ServicioRepository servicioRepository,
                                        TrabajadoresServiciosRepository tsRepository) {
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.tsRepository = tsRepository;
    }

    @PostMapping("/asociar")
    public String asociarTrabajadorServicio(@RequestParam Integer idTrabajador,
                                            @RequestParam Integer idServicio) {
        Usuario trabajador = usuarioRepository.findById(idTrabajador)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        TrabajadorServicio ts = new TrabajadorServicio();
        ts.setTrabajador(trabajador);
        ts.setServicio(servicio);
        tsRepository.save(ts);

        return "Asociación creada correctamente";
    }

    // Listar servicios de un trabajador
    @GetMapping("/listar/{idTrabajador}")
    public List<Servicio> listarServiciosTrabajador(@PathVariable Integer idTrabajador) {
        return tsRepository.findServiciosByTrabajadorId(idTrabajador);
    }

    @GetMapping("/por-servicio/{idServicio}")
    public List<DatosTrabajador> listarTrabajadoresPorServicio(@PathVariable Integer idServicio) {
        return tsRepository.findTrabajadoresByServicioId(idServicio)
                .stream()
                .map(u -> new DatosTrabajador(u.getIdUsuario(), u.getNombre(), u.getApellido()))
                .toList();
    }



}
