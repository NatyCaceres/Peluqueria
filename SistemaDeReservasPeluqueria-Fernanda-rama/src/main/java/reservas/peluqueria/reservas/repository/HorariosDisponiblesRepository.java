package reservas.peluqueria.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import reservas.peluqueria.reservas.entity.HorariosDisponibles;

import java.time.LocalDate;
import java.util.List;

public interface HorariosDisponiblesRepository extends JpaRepository<HorariosDisponibles, Integer> {

    // Todos los horarios de un trabajador (coincide con lo que llama tu controller)
    List<HorariosDisponibles> findByTrabajadorIdUsuario(Integer idTrabajador);

    // Horarios de un trabajador en una fecha específica (coincide con tu controller)
    List<HorariosDisponibles> findByTrabajadorIdUsuarioAndFecha(Integer idTrabajador, LocalDate fecha);
}
