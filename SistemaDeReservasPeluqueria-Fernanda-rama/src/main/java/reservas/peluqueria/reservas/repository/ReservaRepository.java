package reservas.peluqueria.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reservas.peluqueria.reservas.dto.DatosHistorialReserva;
import reservas.peluqueria.reservas.entity.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByIdCliente(Integer idCliente);
    List<Reserva> findByIdTrabajador(Integer idTrabajador);

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.idTrabajador = :idTrabajador
        AND r.fecha = :fecha
        AND r.estadoReserva IN ('PENDIENTE', 'CONFIRMADA')
        AND (
            (:horaInicio BETWEEN r.horaInicio AND r.horaFin)
            OR (:horaFin BETWEEN r.horaInicio AND r.horaFin)
            OR (r.horaInicio BETWEEN :horaInicio AND :horaFin)
        )
    """)
    List<Reserva> verificarSolapamiento(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    @Query("SELECT r FROM Reserva r WHERE r.idTrabajador = :idTrabajador " +
            "AND r.fecha = :fecha " +
            "AND r.idReserva <> :idReserva " +
            "AND ((r.horaInicio < :horaFin AND r.horaFin > :horaInicio))")
    List<Reserva> verificarSolapamientoExcluyendoReserva(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("idReserva") Integer idReserva
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.idTrabajador = :idTrabajador
        AND r.fecha = :fecha
        ORDER BY r.horaInicio
    """)
    List<Reserva> findByTrabajadorAndFecha(
            @Param("idTrabajador") Integer idTrabajador,
            @Param("fecha") LocalDate fecha
    );

    // Métodos antiguos renombrados para que funcionen con la entidad actual
    List<Reserva> findByFecha(LocalDate fecha);

    List<Reserva> findByFechaBetween(LocalDate inicio, LocalDate fin);
    @Query(value = """
    SELECT 
        r.id_reserva AS idReserva,
        s.nombre_servicio AS nombreServicio,
        CONCAT(t.nombre, ' ', t.apellido) AS nombreTrabajador,
        r.fecha,
        r.hora_inicio AS horaInicio,
        r.hora_fin AS horaFin
    FROM reserva r
    JOIN servicio s ON r.id_servicio = s.id_servicio
    JOIN usuario t ON r.id_trabajador = t.id_usuario
    WHERE r.id_cliente = :idCliente
    ORDER BY r.fecha DESC
""", nativeQuery = true)
    List<Map<String, Object>> historialCliente(@Param("idCliente") Integer idCliente);


}
