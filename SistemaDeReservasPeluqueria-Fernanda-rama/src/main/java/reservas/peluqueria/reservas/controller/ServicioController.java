package reservas.peluqueria.reservas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.dto.DatosServicio;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.repository.ReservaRepository;
import reservas.peluqueria.reservas.repository.ServicioRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioRepository servicioRepository;
    private final ReservaRepository reservaRepository;

    public ServicioController(ServicioRepository servicioRepository,
                              ReservaRepository reservaRepository) {
        this.servicioRepository = servicioRepository;
        this.reservaRepository = reservaRepository;
    }

    // ========================
    // CRUD Servicios
    // ========================

    // Crear servicio
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCIONISTA')")
    @PostMapping("/crear")
    public Servicio crearServicio(@RequestBody DatosServicio datos) {
        Servicio servicio = new Servicio();
        servicio.setNombreServicio(datos.nombreServicio());
        servicio.setDescripcion(datos.descripcion());
        servicio.setPrecio(datos.precio());
        servicio.setDuracionEstimadaMinutos(datos.duracionEstimadaMinutos());
        servicio.setActivo(datos.activo());  // tu record siempre tiene valor booleano

        return servicioRepository.save(servicio);
    }

    // Listar todos los servicios activos
    @GetMapping("/activos")
    public List<Servicio> listarServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }

    // Actualizar servicio
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCIONISTA')")
    @PutMapping("/actualizar/{id}")
    public Servicio actualizarServicio(@PathVariable Integer id, @RequestBody DatosServicio datos) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        servicio.setNombreServicio(datos.nombreServicio());
        servicio.setDescripcion(datos.descripcion());
        servicio.setPrecio(datos.precio());
        servicio.setDuracionEstimadaMinutos(datos.duracionEstimadaMinutos());
        servicio.setActivo(datos.activo());

        return servicioRepository.save(servicio);
    }

    // Eliminar servicio
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCIONISTA')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Integer id) {
        servicioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // Reportes de ganancias
    // ========================

    // Ganancias por fecha específica (formato yyyy-MM-dd)
    @GetMapping("/ganancias/{fecha}")
    public double gananciasPorFecha(@PathVariable String fecha) {
        LocalDate localFecha = LocalDate.parse(fecha);

        return reservaRepository.findByFecha(localFecha).stream()
                .filter(r -> "COMPLETADA".equalsIgnoreCase(r.getEstadoReserva()))
                .mapToDouble(r -> {
                    Servicio s = servicioRepository.findById(r.getIdServicio()).orElse(null);
                    return s != null ? s.getPrecio() : 0.0;
                })
                .sum();
    }

    // Ganancias por rango de fechas (yyyy-MM-dd)
    @GetMapping("/ganancias/rango")
    public double gananciasPorRango(@RequestParam String start, @RequestParam String end) {
        LocalDate fechaInicio = LocalDate.parse(start);
        LocalDate fechaFin = LocalDate.parse(end);

        return reservaRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .filter(r -> "COMPLETADA".equalsIgnoreCase(r.getEstadoReserva()))
                .mapToDouble(r -> {
                    Servicio s = servicioRepository.findById(r.getIdServicio()).orElse(null);
                    return s != null ? s.getPrecio() : 0.0;
                })
                .sum();
    }
}
