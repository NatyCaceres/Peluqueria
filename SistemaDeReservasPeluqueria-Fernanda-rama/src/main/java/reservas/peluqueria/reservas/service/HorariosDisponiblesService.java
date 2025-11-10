package reservas.peluqueria.reservas.service;

import org.springframework.stereotype.Service;
import reservas.peluqueria.reservas.entity.HorariosDisponibles;
import reservas.peluqueria.reservas.repository.HorariosDisponiblesRepository;

import java.util.List;

@Service
public class HorariosDisponiblesService {

    private final HorariosDisponiblesRepository horariosRepo;

    public HorariosDisponiblesService(HorariosDisponiblesRepository horariosRepo) {
        this.horariosRepo = horariosRepo;
    }

    public List<HorariosDisponibles> listarTodos() {
        return horariosRepo.findAll();
    }

    public List<HorariosDisponibles> listarPorTrabajador(Integer idTrabajador) {
        return horariosRepo.findByTrabajadorIdUsuario(idTrabajador);
    }

    public HorariosDisponibles crear(HorariosDisponibles horario) {
        // Validar que no haya solapamientos
        List<HorariosDisponibles> existentes = horariosRepo.findByTrabajadorIdUsuarioAndFecha(
                horario.getTrabajador().getIdUsuario(),
                horario.getFecha()
        );

        boolean conflicto = existentes.stream().anyMatch(h ->
                !(horario.getHoraFin().isBefore(h.getHoraInicio()) || horario.getHoraInicio().isAfter(h.getHoraFin()))
        );

        if (conflicto) {
            throw new RuntimeException("El horario se solapa con otro existente");
        }

        return horariosRepo.save(horario);
    }

    public HorariosDisponibles actualizar(Integer id, HorariosDisponibles horarioNuevo) {
        HorariosDisponibles horario = horariosRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setFecha(horarioNuevo.getFecha());
        horario.setHoraInicio(horarioNuevo.getHoraInicio());
        horario.setHoraFin(horarioNuevo.getHoraFin());

        return horariosRepo.save(horario);
    }

    public void eliminar(Integer id) {
        horariosRepo.deleteById(id);
    }
}
