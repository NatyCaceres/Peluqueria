package reservas.peluqueria.reservas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.dto.DatosServicio;
import reservas.peluqueria.reservas.entity.Servicio;
import reservas.peluqueria.reservas.repository.ServicioRepository;

import java.util.List;

@RestController
@RequestMapping("/servicios")
public class ServicioController {
    private final ServicioRepository servicioRepository;

    public ServicioController(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    // Crear servicio
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCIONISTA')")
    @PostMapping("/crear")
    public Servicio crearServicio(@RequestBody DatosServicio datos) {
        Servicio servicio = new Servicio();
        servicio.setNombreServicio(datos.nombreServicio());
        servicio.setDescripcion(datos.descripcion());
        servicio.setPrecio(datos.precio());
        servicio.setDuracionEstimadaMinutos(datos.duracionEstimadaMinutos());
        servicio.setActivo(datos.activo());

        return servicioRepository.save(servicio);
    }
    // Listar todos los servicios activos
    @GetMapping("/activos")
    public List<Servicio> listarServiciosActivos() {
        return servicioRepository.findByActivoTrue();
    }

    // Opcional: eliminar o actualizar
    @PutMapping("/actualizar/{id}")
    public Servicio actualizarServicio(@PathVariable Integer id, @RequestBody Servicio datos) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setNombreServicio(datos.getNombreServicio());
        servicio.setDescripcion(datos.getDescripcion());
        servicio.setPrecio(datos.getPrecio());
        servicio.setDuracionEstimadaMinutos(datos.getDuracionEstimadaMinutos());
        servicio.setActivo(datos.getActivo());
        return servicioRepository.save(servicio);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminarServicio(@PathVariable Integer id) {
        servicioRepository.deleteById(id);
    }
}