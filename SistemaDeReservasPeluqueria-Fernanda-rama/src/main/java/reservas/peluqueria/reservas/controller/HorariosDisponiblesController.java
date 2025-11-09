package reservas.peluqueria.reservas.controller;

import org.springframework.web.bind.annotation.*;
import reservas.peluqueria.reservas.entity.HorariosDisponibles;
import reservas.peluqueria.reservas.repository.HorariosDisponiblesRepository;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/horarios-disponibles")


public class HorariosDisponiblesController {

    private final HorariosDisponiblesRepository horariosRepo;

    public HorariosDisponiblesController(HorariosDisponiblesRepository horariosRepo) {
        this.horariosRepo = horariosRepo;
    }

    // Obtener todos los horarios de un trabajador
    @GetMapping("/trabajador/{idTrabajador}")
    public List<HorariosDisponibles> getHorariosPorTrabajador(@PathVariable Integer idTrabajador) {
        return horariosRepo.findByTrabajadorIdUsuario(idTrabajador);
    }

    // Obtener los horarios de un trabajador en una fecha específica
    @GetMapping("/trabajador/{idTrabajador}/fecha/{fecha}")
    public List<HorariosDisponibles> getHorariosPorTrabajadorYFecha(@PathVariable Integer idTrabajador,
                                                                  @PathVariable String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        return horariosRepo.findByTrabajadorIdUsuarioAndFecha(idTrabajador, fechaParseada);
    }

    // (Opcional) Obtener todos los horarios disponibles sin filtrar
    @GetMapping("/todos")
    public List<HorariosDisponibles> getTodosLosHorarios() {
        return horariosRepo.findAll();
    }
}
