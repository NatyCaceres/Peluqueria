package reservas.peluqueria.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reservas.peluqueria.reservas.entity.HorariosDisponibles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface HorariosDisponiblesRepository extends JpaRepository<HorariosDisponibles, Integer> {

    // Todos los horarios de un trabajador (coincide con lo que llama tu controller)
    List<HorariosDisponibles> findByTrabajadorIdUsuario(Integer idTrabajador);

    // Horarios de un trabajador en una fecha específica (coincide con tu controller)
    List<HorariosDisponibles> findByTrabajadorIdUsuarioAndFecha(Integer idTrabajador, LocalDate fecha);


    @Query("""
        SELECT h FROM HorariosDisponibles h
        WHERE h.trabajador.idUsuario = :idTrabajador
        AND h.fecha = :fecha
        AND :horaInicio >= h.horaInicio
        AND :horaFin <= h.horaFin
    """)
    List<HorariosDisponibles> verificarDisponibilidad(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}
