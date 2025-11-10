package reservas.peluqueria.reservas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DatosReserva(
        Integer idTrabajador,
        Integer idServicio,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String tipoReserva // opcional: "web" o "recepcionista
) {
}
